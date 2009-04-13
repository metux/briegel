
package org.de.metux.briegel.robots;

import org.de.metux.util.rm;
import org.de.metux.briegel.conf.IConfig;

import org.de.metux.briegel.base.EMisconfig;
import org.de.metux.briegel.base.EUnknownBuilder;
import org.de.metux.briegel.base.EBriegelError;
import org.de.metux.briegel.base.EPropertyMissing;
import org.de.metux.briegel.base.EPropertyInvalid;

import org.de.metux.briegel.stages.Stage;
import org.de.metux.briegel.stages.CreateBinpkg;
import org.de.metux.briegel.stages.Prepare;
import org.de.metux.briegel.stages.FetchSource;
import org.de.metux.briegel.stages.IBuilderRun;
import org.de.metux.briegel.stages.PostInstall;

// compiler doesnt want them ... hmm, any way around that ?
//import org.de.metux.briegel.builder_autoconf.Builder;
//import org.de.metux.briegel.builder_gmake.Builder;

import org.de.metux.util.Environment;
import org.de.metux.util.VersionStack;

public class BuildBinpkg extends Stage
{
    public BuildBinpkg(IConfig cf)
	throws EPropertyMissing, EPropertyInvalid
    {
	super("CREATE-BINPKG", cf);
    }

    IBuilderRun get_builder() throws EUnknownBuilder, EMisconfig
    {
	String builder_name = config.getPropertyString("builder");
	if (builder_name.equals("autoconf"))
	    return new org.de.metux.briegel.builder_autoconf.Builder(config);
	    
	if (builder_name.equals("gmake"))
	    return new org.de.metux.briegel.builder_gmake.Builder(config);

//	if (builder_name.equals("treebuild"))
//	    return new org.de.metux.briegel.builder_treebuild.Builder(config);

	throw new EUnknownBuilder(builder_name);
    }
    
    public void run_stage() throws EBriegelError
    {
//	boolean newver_check = false;
//	boolean newver_abort = false;
//	
//	{
//	    String e = Environment.getenv("BRIEGEL_CHECK_NEWER_VERSION");
//	    if ((e!=null)&&(!e.equals("")))
//	    {
//	        if (e.equals("yes"))
//		    newver_check = true;
//		else if (e.equals("abort"))
//		{
//		    newver_check = true;
//		    newver_abort = true;
//		}
//	    }
//	}
	
//	if (newver_check)
//	{
//	    VersionStack vstk = config.getNewerVersions();
//	    if (vstk.count() != 0)
//	    {    
//		warning("Newer versions available: "+vstk);
//		if (newver_abort)
//		    
//
//	}

	/* -- install dependencies -- */
	String req[] = cf_list("require-build");

	config.getPropertyString("system-install-root");
	config.getPropertyString("system-meta-root");
	
	// set the right locations for installing dependencies 
	config.cf_set("@@install-root", "$(system-install-root)");
	config.cf_set("@@meta-root",    "$(system-meta-root)");

	InstallTree instree = new InstallTree(config,req);
	instree.run();

	/* -- prepare and fetch sources -- */
	new FetchSource(config).run();		    
	new Prepare(config).run();

	String instroot = config.getPropertyString("packaging-install-root");
	String metaroot = config.getPropertyString("packaging-meta-root");
	
	if (instroot.length()<5)
	    throw new EMisconfig("$packaging-install-root too short: "+instroot);
	if (metaroot.length()<5)
	    throw new EMisconfig("$packaging-meta-root too short: "+metaroot);
	    
	rm.remove_recursive(instroot);
	rm.remove_recursive(metaroot);
	
	config.cf_set("@@install-root", "$(packaging-install-root)");
	config.cf_set("@@meta-root",    "$(packaging-meta-root");
		
	IBuilderRun builder = get_builder();
	builder.run_preconfig();
	builder.run_configure();
	builder.run_build();
	builder.run_install();
	
	new PostInstall(config).run();
	
	new CreateBinpkg(config).run();
    }
}
