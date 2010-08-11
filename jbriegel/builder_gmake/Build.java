
package org.de.metux.briegel.builder_gmake;

import java.io.File;

import org.de.metux.util.StoreFile;

import org.de.metux.briegel.base.EPropertyInvalid;
import org.de.metux.briegel.base.EPropertyMissing;
import org.de.metux.briegel.base.EMisconfig;
import org.de.metux.briegel.base.EBuildFailed;
import org.de.metux.briegel.conf.IConfig;
import org.de.metux.briegel.conf.ConfigNames;
import org.de.metux.briegel.stages.Stage;
import org.de.metux.briegel.stages.IBuilderRun;

public class Build extends Stage
{
    public Build(IConfig cf)
	throws EPropertyInvalid, EPropertyMissing
    {
	super("GMAKE-BUILD",cf);
    }
    
    public void run_stage() throws EMisconfig, EBuildFailed
    {
	String workdir    = config.cf_get_str_mandatory(ConfigNames.SP_WorkingDir);
	String build_exec = config.getPropertyString("gmake-exec-build");
	String build_env  = config.getPropertyString("gmake-env-build");

	String cmdline = "cd "+workdir+" && "+build_env+" "+build_exec;
	
	if (!StoreFile.store(workdir+"/BRIEGEL-cmd-build", cmdline, "ugo+rx"))
	    throw new EBuildFailed("could not store cmd file: ");
	
	if (!exec(cmdline))
	    throw new EBuildFailed("gmake step failed");
    }
}
