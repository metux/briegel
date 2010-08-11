
package org.de.metux.briegel.builder_treebuild;

import org.de.metux.briegel.conf.IConfig;
import org.de.metux.briegel.conf.ConfigNames;
import org.de.metux.pi_build.main.TreeBuilder;
import org.de.metux.pi_build.nodes.PackageNode;
import org.de.metux.util.FileOps;
import java.io.File;
import org.de.metux.briegel.stages.Stage;
import org.de.metux.briegel.stages.IBuilderRun;
import org.de.metux.briegel.base.EMissingDependency;
import org.de.metux.briegel.base.EBriegelError;
import org.de.metux.briegel.base.EBuildFailed;
import org.de.metux.briegel.base.EMisconfig;
import org.de.metux.briegel.base.EInstallFailed;
import org.de.metux.briegel.base.EPropertyInvalid;
import org.de.metux.briegel.base.EPropertyMissing;

public class Builder extends Stage implements IBuilderRun
{
    TreeBuilder treebuild;
    PackageNode pkg;
    String      current_working_dir;

    class InstallStage extends Stage
    {
	public InstallStage(IConfig cf)
	    throws EPropertyMissing, EPropertyInvalid
        {
	    super("TREEBUILD-INSTALL", cf);
	}

	public void run_stage() throws EMisconfig, EInstallFailed
	{
	    notice("workdir is: "+current_working_dir);

	    String oldwd = FileOps.getcwd();
	    FileOps.chdir(current_working_dir);
	    pkg.run_Install();
	    FileOps.chdir(oldwd);
    	}
    }

    class BuildStage extends Stage
    {
	public BuildStage(IConfig cf)
	    throws EPropertyMissing, EPropertyInvalid
	{
	    super("TREEBUILD-BUILD",cf);
	}
    
	public void run_stage() throws EMisconfig, EBuildFailed
	{
	    notice("workdir is: "+current_working_dir);

	    String oldwd = FileOps.getcwd();
	    FileOps.chdir(current_working_dir);
	    pkg.run_Autodep();

	    try
	    {	    
		pkg.run_Build();
	    }
	    catch (Exception e)
	    {
		throw new EBuildFailed(current_port_name,e);
	    }
	    FileOps.chdir(oldwd);
	}
    }

    class ConfigureStage extends Stage
    {
	public ConfigureStage(IConfig cf)
	    throws EPropertyInvalid, EPropertyMissing
	{
	    super("TREEBUILD-CONFIGURE",cf);
	}

	public void run_stage() 
	    throws EPropertyInvalid, EPropertyMissing, EMissingDependency
	{
	    String oldwd = FileOps.getcwd();
	    FileOps.chdir(current_working_dir);
	    try
	    {
		pkg.run_Configure();
	        pkg.run_Autodep();
	    } catch (metux.pi_build.base.EDependencyMissing e)
	    {
		throw new EMissingDependency(current_port_name);
	    }
	    FileOps.chdir(oldwd);
	}
    }
    
    public Builder(IConfig cf)
	throws EPropertyMissing, EPropertyInvalid
    {
	super("TREEBUILD",cf);
    }

    void _cp_instdir(String name)
	throws EPropertyInvalid
    {
	String val = config.cf_get_str("package-install-"+name);
	System.err.println("setting ["+name+"] to \""+val+"\"");
	pkg.setProperty(
	    "@PACKAGE/install-"+name,
	    config.cf_get_str("package-install-"+name));
    }
    
    public void run_preconfig()	throws EBriegelError
    {
	notice("builder: treebuild/j v0.1" );

	/* check if make rules for install/install-strip are defined */
	config.cf_set(ConfigNames.SP_WorkingDir, "$(@@srcdir)/$(treebuild-subdir)");
	current_working_dir = config.cf_get_str_mandatory(ConfigNames.SP_CurrentWorkingDir);

	try
	{
	    String oldwd = FileOps.getcwd();
	    FileOps.chdir(current_working_dir);

	    /* load the actual builder stuff */
	    treebuild = new TreeBuilder(
		new File(config.getPropertyString("treebuild-config")));
	    pkg = treebuild.loadPackage(
		new File(config.getPropertyString("treebuild-profile")));

	    FileOps.chdir(oldwd);
	} 
	catch (Exception e)
	{
	    throw new EBriegelError(current_port_name,e);
	}

	// pass the install-root (aka DESTDIR) directory to the TreeBuild instance */
	pkg.setProperty("@@install-root", config.cf_get_str_mandatory(ConfigNames.SP_InstallRoot));

	_cp_instdir("prefix");
	_cp_instdir("bindir");
	_cp_instdir("sbindir");
	_cp_instdir("libexecdir");
	_cp_instdir("mandir");
	_cp_instdir("infodir");
	_cp_instdir("libdir");
	_cp_instdir("localstatedir");
	_cp_instdir("sysconfdir");
	_cp_instdir("datadir");
	_cp_instdir("x11-prefix");
	_cp_instdir("x11-bindir");
	_cp_instdir("x11-sbindir");
	_cp_instdir("x11-libexecdir");
	_cp_instdir("x11-mandir");
	_cp_instdir("x11-infodir");
	_cp_instdir("x11-libdir");
	_cp_instdir("x11-localstatedir");
	_cp_instdir("x11-sysconfdir");
	_cp_instdir("x11-datadir");
    }
    
    public void run_configure()	throws EBriegelError
    {
	new ConfigureStage(config).run();
    }
    
    public void run_build() throws EBriegelError
    {
	new BuildStage(config).run();
    }
    
    public void run_install() throws EBriegelError
    {
	new InstallStage(config).run();
    }
    
    public void run_stage() throws EBriegelError
    {
	run_preconfig();
	run_configure();
	run_build();
	run_install();
    }
}
