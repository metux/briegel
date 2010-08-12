
package org.de.metux.briegel.stages;

import java.io.File;
import java.io.IOException;

import org.de.metux.util.rm;
import org.de.metux.util.StrUtil;

import org.de.metux.briegel.base.EPrepareFailed;
import org.de.metux.briegel.base.EMisconfig;
import org.de.metux.briegel.base.EPropertyInvalid;
import org.de.metux.briegel.base.EPropertyMissing;

import org.de.metux.briegel.conf.IConfig;
import org.de.metux.briegel.conf.ConfigNames;

public class Prepare extends Stage
{
    protected String filename;
    private   String buildroot;

    public Prepare(IConfig cf)
	throws EPropertyMissing, EPropertyInvalid
    {
	super("PREPARE",cf);
    }

    public void run_stage() throws EPrepareFailed, EMisconfig, EPropertyInvalid
    {
	/* -- add some global vars -- */
	config.cf_set(ConfigNames.SP_BuildRoot, "$(source-buildroot)");
	config.cf_set(ConfigNames.SP_SrcRoot,   "$("+ConfigNames.SP_BuildRoot+")/$("+ConfigNames.SP_SrcTree+")");
	config.cf_set(ConfigNames.SP_SrcDir,    "$("+ConfigNames.SP_SrcRoot+")/$(source-prefix)");

	/* -- prepare the buildroot and do paranoia checks -- */
	buildroot = config.cf_get_str(ConfigNames.SP_BuildRoot);
	if (buildroot.length()<10)
	    throw new EMisconfig("Sanity check failed: buildroot too short (<10)");

	try
	{
	    buildroot = new File(buildroot).getCanonicalPath();
	}
	catch (IOException e)
	{
	    error("exception: "+e);
	    throw new EPrepareFailed("cannot access buildroot: \""+buildroot+"\"");
	}

	if (buildroot.length()<10)
	    throw new EMisconfig("Sanity check failed: normalized buildroot too short (<10)");

	/* -- prepare the sourcetree -- */
	prepare_sourcetree();

	/* -- init directories -- */
	String [] init_dirs = cf_list("init-dirs");
	for (int x=0; x<init_dirs.length; x++)
	    mkdir(init_dirs[x]);

	debug("source-prefix:  "+config.getPropertyString("source-prefix",""));
	debug(ConfigNames.SP_BuildRoot+":    "+config.cf_get_str(ConfigNames.SP_BuildRoot));
	debug(ConfigNames.SP_SrcRoot+":      "+config.cf_get_str(ConfigNames.SP_SrcRoot));
	debug(ConfigNames.SP_SrcDir+":       "+config.cf_get_str(ConfigNames.SP_SrcDir));
	debug(ConfigNames.SP_SrcTree+":      "+config.cf_get_str(ConfigNames.SP_SrcTree));
    }

    String detect_archiver ( String filename )
    {
	if (filename.endsWith(".tar.gz") || 
	    filename.endsWith(".tgz"))
	    return "tar.gz";
	    
	if (filename.endsWith(".tar.bz2") ||
	    filename.endsWith(".tbz") ||
	    filename.endsWith(".tbz2"))
	    return "tar.bz2";
	    
	return null;
    }
    
    void decompress_archive ( String source ) throws EMisconfig, EPrepareFailed
    {
	String archiver = config.getPropertyString("archiver","");
	if (archiver.length()==0)
	{
	    if ((archiver=detect_archiver(source))==null)
	    {
		error("cannot detect proper archiver for \""+source+"\"");
		throw new EMisconfig("cannot detect proper archiver for: "+source);
	    }
	    config.cf_set("archiver", archiver);
	}	    
    
	if (!mkdir(buildroot))
	    throw new EPrepareFailed ("cannot create buildroot");

	if (archiver.equals("tar.bz2"))
	{
	    notice("decompressing tar.bz2: "+source+" ..." );
	    if (!exec ( "cd "+buildroot+" && tar -xjf "+source ))
		throw new EPrepareFailed("error decompressing: "+source);
	}
	else if (archiver.equals("tar.gz"))
	{
	    notice("decompressing tar.gz: "+source+" ..." );
	    if (!exec ( "cd "+buildroot+" && tar -xzf "+source ))
		throw new EPrepareFailed("error decompressing: "+source);
	}
	else throw new EMisconfig("unsupported archiver: \""+archiver+"\"" );
    }

    /* pre-execute something *before* we start decompressing
       ie. for preparing patch sets */
       
    private void preexec() throws EPrepareFailed, EPropertyInvalid, EPropertyMissing
    {
	/* prepare patches */
	String preexec=config.getPropertyString("prebuild-patch-preexec","");
	if (preexec.length()==0)
	    return;
	    
	notice("Calling preexec: "+preexec);
	if (exec(preexec))
	    return;
	    
	error("preexec failed");
	throw new EPrepareFailed("preexec failed");
    }

    void cleanup() throws EMisconfig
    {
	notice("cleaning up "+buildroot );
	rm.remove_recursive(buildroot);
	mkdir(buildroot);
    }

    void decompress() throws EMisconfig, EPrepareFailed
    {
	/* now run decompression */
	String srclist[] =cf_list("source-file");
	if (srclist.length==0)
	{
	    error("missing package source option in configuration");
	    throw new EMisconfig("missing package source option in configuration");
	}

	for (int x=0; x<srclist.length; x++)
	    decompress_archive(srclist[x]);
    }
        
    void prepare_sourcetree() throws EMisconfig, EPrepareFailed
    {
	preexec();
	cleanup();
	decompress();
	lookup_srcdir();
	apply_patches();
    }

    /* right after decompression, we need to know our actual srcroot */
    void lookup_srcdir() throws EMisconfig, EPrepareFailed
    {
	File handle = new File(buildroot);
	String sublist[] = handle.list();
	
	if (sublist==null) 
	{
	    error("could not get directory listing!");
	    throw new EPrepareFailed("could not get directory listing!");
	}
	
	if (sublist.length==0)
	{
	    error("no srcdir found. seems like decompression failed");
	    throw new EPrepareFailed("no srcdir found. seems like decompression failed");
	}
		
	if (sublist.length>1)
	{
	    error("more than one srcdir found. looks like trouble");
	    throw new EPrepareFailed("more than one srcdir found. looks like trouble");
	}
	
	debug("srcdir is: "+sublist[0]);
	
	String srcroot = buildroot+"/"+sublist[0];
	debug("srcroot is: "+srcroot);
	
	config.cf_set(ConfigNames.SP_SrcTree, sublist[0]);
    }
    
    void apply_patches() throws EMisconfig, EPrepareFailed
    {
	String patches[]  = cf_list("prebuild-patch-file");
	String patch_opts = config.getPropertyString("prebuild-patch-opts","");
	String srcroot    = config.cf_get_str_mandatory(ConfigNames.SP_SrcRoot);

	for (int x=0; x<patches.length; x++)
	{
	    if (!(new File(patches[x]).exists()))
	    {
		error("could not load patch file: "+patches[x]);
		throw new EPrepareFailed ("could not load patch file: "+patches[x]);
	    }

	    notice("applying patch: "+patches[x]+"("+patch_opts+")" );
	    if (!exec("cd "+srcroot+";pwd;patch "+patch_opts+"<"+patches[x]))
	    {
		error("could not apply patch");
		throw new EPrepareFailed("could not apply patch");
	    }
	}
    }
}
