package org.de.metux.briegel.conf;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Properties;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.File;
import java.io.IOException;

import org.de.metux.util.LoadFile;
import org.de.metux.util.Filename;

import org.de.metux.util.UniqueNameList;
import org.de.metux.util.StrReplace;
import org.de.metux.util.Depend;
import org.de.metux.util.StoreFile;
import org.de.metux.util.Version;
import org.de.metux.util.VersionStack;
import org.de.metux.util.Environment;

import org.de.metux.datasource.TextTable;
import org.de.metux.datasource.Cached_TextTable_Loader;
import org.de.metux.datasource.Cached_TextDB_Loader;
import org.de.metux.datasource.Cached_Content_Loader;

import org.de.metux.propertylist.IPropertylist;
import org.de.metux.propertylist.IPostprocessor;
import org.de.metux.propertylist.EIllegalValue;
import org.de.metux.propertylist.Propertylist;
import org.de.metux.propertylist.EProcessingError;
import org.de.metux.propertylist.EVariableNull;
import org.de.metux.propertylist.EVariableEmpty;

import org.de.metux.log.ILogger;
import org.de.metux.log.LoglevelID;
import org.de.metux.briegel.base.FBriegelLogger;

import org.de.metux.briegel.base.EBriegelError;
import org.de.metux.briegel.base.EMisconfig;
import org.de.metux.briegel.base.EPropertyMissing;
import org.de.metux.briegel.base.EPropertyInvalid;
import org.de.metux.briegel.base.EMissingStyle;
import org.de.metux.briegel.base.EMissingPort;
import org.de.metux.briegel.base.EMissingFeatureSwitch;
import org.de.metux.briegel.base.EMissingPackage;
import org.de.metux.briegel.base.EMissingGlobalConf;
import org.de.metux.briegel.base.EMissingFeatureDB;

public class BriegelConf implements IConfig
{
    Propertylist   thisconf;
    public ILogger logger;

    public static final String k_csdb_query_versions="csdb-query-versions";
    private static final String cf_world_filename="world-file";
    private static final String cf_csdb_query_versions_url="csdb-query-versions-url";

    /* SP_xx means: system property -- always filled by the system, RO ! */
    private static final String SP_version     = "@@version";
    private static final String SP_feature_tag = "@@feature-tag";
    private static final String SP_db_world    = "@@world";
    private static final String SP_csdb_available_versions="@@csdb-available-versions";
    private static final String SP_globalconf   = "@@global-config-file";
    
    boolean processed_csdb_versions = false;
    boolean processed_db_world      = false;
    boolean processed_db_system     = false;

	static Cached_TextTable_Loader ttloader = new Cached_TextTable_Loader();
	static Cached_TextDB_Loader textdb_loader = new Cached_TextDB_Loader();
	static Cached_Content_Loader content_loader = new Cached_Content_Loader();

    private class postprocessor implements IPostprocessor
    {
	public boolean propertylist_postprocess(IPropertylist pl)
	    throws EIllegalValue
	{
	    throw new RuntimeException ( "should never be reached! ");
	}
    }

    private boolean __load_properties_sub(String fn)
    {
	Properties pr = textdb_loader.load(fn);
	if (pr == null)
	    return false;
	thisconf.loadProperties_sub(pr);
	return true;
    }

    private boolean __load_properties_top(String fn)
    {
	Properties pr = textdb_loader.load(fn);
	if (pr == null)
	    return false;
	thisconf.loadProperties_top(pr);
	return true;
    }

    /* --- special handling for @style directives --- */
    private boolean process_styles() 
	throws EMissingStyle, EPropertyInvalid, EPropertyMissing
    {
	boolean gotsome = false;
	String loaded_styles = "";
	String stylepath;
	    
	stylepath=cf_get_str_mandatory("style-filename");
	
	while (true)
	{
	    String style_list[] = cf_get_list("@style");
	    cf_remove("@style");

	    if (style_list.length==0)
		break;
		
	    gotsome = true;
	    for (int x=0; x<style_list.length; x++)
	    {
		String fn = StrReplace.replace
		    ("{STYLE}", style_list[x], stylepath);
		if (!__load_properties_sub(fn))
		    throw new EMissingStyle(style_list[x]);

		loaded_styles += style_list[x] + " ";
	    }
	}	
	
	if (gotsome)
	{
	    debug("Loaded styles: "+loaded_styles);
	    return true;
	}
	else 
	    return false;
    }    

    private boolean process_world_db()
	throws EPropertyMissing, EPropertyInvalid
    {
	if (processed_db_world)
	    return false;

	cf_load_content(
	    SP_db_world,
	    getPropertyString(cf_world_filename));

	processed_db_world = true;
	return true;
    }

    private boolean process_csdb()
	throws EPropertyInvalid, EPropertyMissing
    {
	if ((!cf_get_boolean(k_csdb_query_versions,false)) ||
		processed_csdb_versions)
	    return false;

	debug("checking for new versions ... ");
	processed_csdb_versions = true;

	return cf_load_content(
	    SP_csdb_available_versions,
	    getPropertyURL(cf_csdb_query_versions_url));
    }

    private boolean process_package()
    	throws EPropertyInvalid, EPropertyMissing, EMissingPackage, EMissingStyle
    {
	String pkgname;

	if ((pkgname = cf_get_str_n("@package"))==null)
		return false;

	cf_remove("@package");
	cf_set(ConfigNames.SP_PackageName, pkgname);
	
	String dir    = cf_get_str_mandatory("package-db-dir");
	String fn     = cf_get_str_mandatory("package-db-conf");

	notice("Loading package "+pkgname+" ("+fn+")");
	
	cf_set("@@package-db-conf", fn);
	cf_set("@@package-db-dir", dir);
	cf_set(ConfigNames.SP_PackageName, pkgname);

	/* NOTE: we have changed the load order of package.cf and 
	   package-$version.cf, so we can define its name in package.cf
           
	   we can relax now at this point, since we now are able to 
	   overwrite properties by prepending "!!"
	  
	   Probably some package profiles have to be changed to work again. 
	*/

	if (!__load_properties_sub(fn))
	    throw new EMissingPackage(pkgname);

	run_postprocess();
	
	String fn_ver = cf_get_str_mandatory("package-db-version-conf");
	if (__load_properties_sub(fn_ver))
	    debug("loaded package version cf "+fn_ver);
	else
	    debug("no version package cf "+fn_ver);

	run_postprocess();

	// dump available versions
	{
	    VersionStack vst_avail = getAvailableVersions();
	    if ((vst_avail.size()==0) &&
	        cf_get_boolean("warn-missing-available-versions"))
		warning("missing \"available-versions\" property !");
	    else
	    {
		VersionStack vst_higher = new VersionStack();
	    	VersionStack vst_lower = new VersionStack();
		vst_avail.divide(getVersion(),vst_lower,vst_higher);

		if (cf_get_boolean("debug-available-versions",false))
		{
		    debug("available-versions: "+vst_avail.toString());
		    debug("lower-versions:     "+vst_lower.toString());
	    	    debug("higher-versions:    "+vst_higher.toString());
		}

		if (cf_get_boolean("notice-higher-versions",true) &&
		    (vst_higher.size()>0))
		    notice("Higher versions available: "+vst_higher.toString());
	    }
	}
	return true;
    }

    public VersionStack getAvailableVersions()
	throws EPropertyInvalid
    {
	return new VersionStack(cf_get_list("available-version"));    
    }

    public VersionStack getNewerVersions()
	throws EPropertyInvalid, EPropertyMissing
    {
	VersionStack vst_avail = getAvailableVersions();
	VersionStack vst_newer = new VersionStack();
	vst_avail.divide(getVersion(),null,vst_newer);
	return vst_newer;
    }

    private void run_postprocess()
	throws EMissingPackage, EPropertyInvalid, EPropertyMissing, EMissingStyle
    {
	while (
	    process_package() ||
	    process_styles()  ||
	    process_csdb()    ||
	    process_world_db()
	);
    }
    
    public ILogger getLogger()
    {
	return logger;
    }
    
    public IConfig allocConfig()
	throws EMisconfig
    {
	return new BriegelConf(
	    cf_get_str(SP_globalconf),
	    logger
	);
    }

    // allocate a new BriegelConf object with the same globalconf 
//    public /*IConfig*/ BriegelConf alloc()
    public IConfig alloc()
	throws EMisconfig
    {
	return new BriegelConf(
	    cf_get_str_mandatory(SP_globalconf),
	    logger
	);
    }

    // same as alloc(), but returns an BriegelConf type instead of IConfig
    public BriegelConf alloc_BriegelConf()
	throws EMisconfig
    {
	return new BriegelConf(
	    cf_get_str_mandatory(SP_globalconf),
	    logger
	);
    }

    public IConfig allocLoadPort(String port)
	throws EMisconfig, EMissingPort
    {
	BriegelConf cf = alloc_BriegelConf();
	cf.LoadPort(port);
	return cf;
    }
	    
    public BriegelConf(String configfile, ILogger new_logger)
	throws EMisconfig
    {
	/* must be the very first */
	logger     = new_logger;
	logger.setLogLevel(LoglevelID.toID(Environment.getenv("BRIEGEL_LOGLEVEL")));
	thisconf   = new Propertylist();

	/* now load the environment */
	Environment env = new Environment();
	for (Enumeration e=env.keys(); e.hasMoreElements();)
	{
	    String key = (String)e.nextElement();
	    String val = (String)env.get(key);
	    debug("Adding env variable "+key+"=\""+val+"\"");
	    thisconf.set("@@ENV/"+key,val);
	}

	thisconf.setPostprocessor(new postprocessor());
	
	thisconf.set(SP_globalconf, configfile);
	debug("loading global config: "+configfile);

	if (!__load_properties_sub(configfile))
	    throw new EMissingGlobalConf(configfile);

	cf_add("@style", cf_get_str("preload-style"));
	run_postprocess();
	
    }

    // configfile: name of the global config file
    public BriegelConf(String configfile)
	throws EMisconfig
    {
	this(configfile,FBriegelLogger.alloc());
    }
    
    public Version getVersion()
	throws EPropertyMissing, EPropertyInvalid
    {
	return new Version(cf_get_str_mandatory(SP_version));
    }

    private void process_version()
	throws EMisconfig
    {
	/* process @version statement */
	String version[] = cf_get_list("@version");
	if (version.length==0)
	    throw new EPropertyMissing("@version");
	if (version.length>1)
	    throw new EMisconfig("more than one @version statement not allowed");

	// fixme: we could do a parse test here
	cf_remove("@version");
        notice("Got version: "+version[0]);
	
	try
	{
	    Version ver = new Version(version[0]);
	    cf_set("@@version0", String.valueOf(ver.digits[0]));
	    cf_set("@@version1", String.valueOf(ver.digits[1]));
	    cf_set("@@version2", String.valueOf(ver.digits[2]));
	    cf_set("@@version3", String.valueOf(ver.digits[3]));
	    cf_set(SP_version, version[0]);
	}
	catch (NumberFormatException e)
	{
	    throw new EMisconfig("version format violation: \""+version+"\"",e);
	}
    }

    public URL getPropertyURL(String name)
	throws EPropertyMissing, EPropertyInvalid
    {
	String val = cf_get_str_mandatory(name);
	try
	{
	    if ((val==null)||(val.length()==0))
		throw new EPropertyMissing(name);
//	    System.err.println("PropertyURL: "+val);

	    if (val.startsWith("/"))
	        return new URL ("file://"+val);
	    return new URL(val);
	}
	catch (MalformedURLException e)
	{
	    throw new EPropertyInvalid(name,e);
	}
    }

    public URL getPropertyURL(String name, URL def)
	throws EPropertyInvalid
    {
	try
	{
	    return getPropertyURL(name);
	}
	catch (EPropertyMissing e)
	{
	    return def;
	}
    }

    public boolean LoadPort ( String port_ident )
	throws EMisconfig, EMissingPort
    {
	Depend dep = new Depend (port_ident);

	/* actual port-loading stuff */
	cf_set(ConfigNames.SP_PortName, dep.package_name );
	
	/* load master port config */ 
	String port_fn = cf_get_str_mandatory("port-db-conf");
	notice("Loading port: "+dep.package_name+" ("+port_fn+")");
        if (!__load_properties_top(port_fn))
	    throw new EMissingPort(port_fn);

	process_version();

	cf_set("[GLOBAL]"+ConfigNames.SP_PackageName, "$("+ConfigNames.SP_PackageName+")");

	String port_ver_fn = cf_get_str_mandatory("port-db-version-conf");
	cf_set("@@port-db-version-conf", port_ver_fn);

	if (__load_properties_top(port_ver_fn))
	    notice("Loaded port version config: "+port_ver_fn);
//	else
//	    warning("Missing port version config: "+port_ver_fn);
	    
	run_postprocess();
	load_feature_db();
	run_postprocess();
	process_features();
	run_postprocess();
	process_required_settings();

	cf_set("@@id", "$("+ConfigNames.SP_PortName+")");

        return true;
    }

    public String[] getWorldList()
	throws EPropertyInvalid
    {
	return cf_get_list(SP_db_world);
    }

    public final void error ( String text )
    {
	logger.error ( "BUILDCONF", text );
    }
    public final void warning ( String text )
    {
	logger.warning ( "BUILDCONF", text );
    }
    public final void notice ( String text )
    {
	logger.notice ( "BUILDCONF", text );
    }
    public final void debug ( String text )
    {	
        logger.debug ( "BUILDCONF", text );
    }

    public void store(String name)
    {
	String text = thisconf.dump();
	StoreFile.store(name,text);
    }
    
    public void cf_set(String name, String value)
    {
	thisconf.set(name,value);
    }

    public void cf_set(String name, boolean value)
    {
	thisconf.set(name,(value ? "yes" : "no"));
    }
    
    public String cf_get_str(String name)
	throws EPropertyInvalid
    {
	try
	{
	    return thisconf.get_str(name);
	}
	catch (EIllegalValue e)
	{
	    throw new EPropertyInvalid(name,e);
	}
    }

    public String cf_get_str(String name, String def)
    {
	try
	{
	    return thisconf.get_str(name);
	}
	catch (EIllegalValue e)
	{
	    return def;
	}
    }

    public String cf_get_str_n(String name)
	throws EPropertyInvalid
    {
	String v = cf_get_str(name);
	v.trim();
	if (v.length()<1)
	    return null;
	return v;
    }

    public String cf_get_str_mandatory(String name)
	throws EPropertyMissing, EPropertyInvalid
    {
	String val = cf_get_str_n(name);
	if (val==null)
	    throw new EPropertyMissing(name);
	return val;
    }

    public String getPropertyString(String name, String defval)
	throws EPropertyInvalid, EPropertyMissing
    {
	String value;
	try
	{
	    value = thisconf.get_str(name);
	}

	/* throw sane exceptions of referenced variables violate constraints */
	catch (EVariableNull e)
	{
	    throw new EPropertyMissing(e.name);
	}
	catch (EVariableEmpty e)
	{
	    throw new EPropertyMissing(e.name);
	}

	/* throw exception if recursive parsing failed */
	catch (EIllegalValue e)
	{
	    throw new EPropertyInvalid(name,e);
	}

	if ((value==null)||(value.length()==0))
	    return defval;
	    
	return value;
    }

    public String getPropertyString(String name)
	throws EPropertyInvalid, EPropertyMissing
    {
	String value = getPropertyString(name,null);
	if (value==null)
	    throw new EPropertyMissing(name);
	return value;
    }	

    public boolean cf_get_boolean(String name)
	throws EPropertyInvalid
    {
	try
	{
	    return thisconf.get_bool(name);
	}
	catch (EIllegalValue e)
	{
	    throw new EPropertyInvalid(name,e);
	}
    }

    public boolean cf_get_boolean(String name, boolean def)
    {
	return thisconf.get_bool(name,def);
    }
    
    public void cf_add(String name, String value)
    {
	thisconf.add(name,value);
    }

    public void cf_remove(String name)
    {
	thisconf.remove(name);
    }

    public String [] cf_get_list(String name) 
	throws EPropertyInvalid
    {
	try {
	    return thisconf.get_list(name);
	}
	catch (EIllegalValue e) {
	    throw new EPropertyInvalid(name,e);
	}
    }

    public boolean cf_load_content(String property, String filename)
    {
	String s = content_loader.load(filename, true);
	if (s == null)
	    return false;
	thisconf.set(property, s);
	return true;
    }

    public boolean cf_load_content(String property, File filename)
    {
	String s = content_loader.load(filename, true);
	if (s == null)
	    return false;
	thisconf.set(property, s);
	return true;
    }

    public boolean cf_load_content(String property, URL url)
    {
	String s = content_loader.load(url, true);
	if (s == null)
	    return false;
	thisconf.set(property, s);
	return true;
    }

    private void process_required_settings()
	throws EPropertyMissing, EPropertyInvalid
    {
	boolean missing = false;
	String vars = "";
	String req[] = cf_get_list("require-setting");
	
	debug("Amount of required settings: "+req.length);
	
	for (int x=0; x<req.length; x++)
	{
	    try
	    {
		cf_get_str_mandatory(req[x]);
	        debug("required setting \""+req[x]+"\" ... ok" );
	    }
	    catch (EPropertyMissing e)
	    {
		error( "required setting \""+req[x]+"\" ... MISSING !");
		if (missing)
		    vars += ",";
		vars += req[x];
		missing = true;
	    }
	}
	
	if (missing)
	    throw new EPropertyMissing(vars);
    }

	/* --- Feature DB loading --- */

	private TextTable cache_feature_db(String fn)
		throws EMissingFeatureDB
	{
		try
		{
			TextTable tt = ttloader.loadTextTable(new Filename(fn),"feature");
			return tt;
		}
		catch (MalformedURLException e)
		{
			throw new EMissingFeatureDB(fn+" "+e.toString());
		}
		catch (IOException e)
		{
			throw new EMissingFeatureDB(fn+" "+e.toString());
		}
	}

    private boolean load_feature_db()
	throws EPropertyMissing, EPropertyInvalid, EMissingFeatureDB
    {
	boolean res = true;

	String feature_db_srcs[] = cf_get_list("feature-db");
	String add_vars[] = cf_get_list("feature-addvar");
	String def_var = cf_get_str_mandatory("feature-defvar");

	for (int a=0; a<feature_db_srcs.length; a++)
	{
	    TextTable h = cache_feature_db(feature_db_srcs[a]);

	    if (add_vars.length==0)
		throw new EPropertyMissing("feature-addvar");

	    if (h != null)
	    {
		for (Enumeration e=h.keys(); e.hasMoreElements(); )
		{
		    String feature = (String)e.nextElement();
		    Properties ent = h.get(feature);
		    cf_add("feature-declare", feature);
		    cf_add("feature-addvar-on="+feature+"="+def_var,ent.getProperty("on"));
		    cf_add("feature-addvar-off="+feature+"="+def_var,ent.getProperty("off"));
		    cf_add("feature-addvar-on="+feature+"=require-build",ent.getProperty("on=require-build"));
		    cf_add("feature-addvar-off="+feature+"require-build",ent.getProperty("off=require-build"));
		    // The feature id is required for building the feature-tag
		    // per default we use the feature name, but sometimes we end up in an too long archive file
		    // name when using the (often quite verbose) feature names. To fix this, we can optionally
		    // define (much shorter) feature-id's, which are not visible to the user
		    String feature_id = ent.getProperty("id");
		    if (feature_id==null)
			feature_id = feature;
		    cf_add("feature-id="+feature,feature_id);
		}
	    }
	}
	return res;
    }

    /* --- feature processing -> map variables of selected features
                                 to global scope --- */
    /* 
	this function renders the effective package/port configuration 
	from enabled features
    */

    private String fix_feature_name(String name)
    {
    	return name.replace('/','_'); 
    }

    private boolean __cf_is_set(String name)
	throws EPropertyInvalid
    {
	return cf_get_str_n(name) != null;
    }

    private boolean __is_feature_enabled(String name)
	throws EPropertyInvalid
    {
	String k = "feature-enable="+name;
	return cf_get_boolean((__cf_is_set(k) ? k : "@@"+k));
    }

    private void process_features()
	throws EMissingFeatureSwitch, EPropertyMissing,
	    EPropertyInvalid
    {
	String add_vars[]     = cf_get_list("feature-addvar");
	String def_addvar     = cf_get_str_mandatory("feature-defvar");
	String feature_decl[] = cf_get_list("feature-declare");
	String missing        = null;
	String feature_tag    = "";

	for (int x=0; x<feature_decl.length; x++)
	{
	    String feature = feature_decl[x];
	    try
	    {
		boolean enabled = __is_feature_enabled(feature);
		String prefix = (enabled ? "feature-addvar-on=" : "feature-addvar-off=")+feature+"=";

		feature_tag += (enabled ? "+" : "-")+fix_feature_name(cf_get_str("feature-id="+feature));

		cf_add(def_addvar, cf_get_str(prefix+def_addvar));

		/* now walk through all addvars and get them in */
		// FIXME: perhaps we should use $(feature-...) ?
		for (int y=0; y<add_vars.length; y++)
		    cf_add(
			add_vars[y],
			cf_get_str(prefix+add_vars[y]
		    ));
	    }
	    catch (EPropertyInvalid e)
	    {
		error("Feature \""+feature+"\" UNDEFINED!");
		if (missing==null)
		    missing = feature;
		else
		    missing += ","+feature;
	    }	
	}

	if (feature_tag.length()>0)
	{
	    notice("Feature-Tag: "+feature_tag);
	    cf_add(SP_feature_tag,feature_tag);
	}

	if (missing!=null)
	    throw new EMissingFeatureSwitch(getPortName(), missing);
    }

    /* retrieve the current port's name */
    public String getPortName()
	throws EPropertyMissing, EPropertyInvalid
    {
	return cf_get_str_mandatory(ConfigNames.SP_PortName);
    }
}
