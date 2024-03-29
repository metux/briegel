
package org.de.metux.briegel.builder_autoconf;

import java.io.File;
import org.de.metux.util.StoreFile;
import org.de.metux.briegel.stages.Stage;
import org.de.metux.briegel.base.EPropertyMissing;
import org.de.metux.briegel.base.EPropertyInvalid;
import org.de.metux.briegel.base.EMisconfig;
import org.de.metux.briegel.base.EBuildFailed;
import org.de.metux.briegel.conf.IConfig;
import org.de.metux.briegel.conf.ConfigNames;

public class Build extends Stage
{
    public Build(IConfig cf)
	throws EPropertyMissing, EPropertyInvalid
    {
	super("AC-BUILD", cf);
    }
    
    public void run_stage() throws EMisconfig, EBuildFailed
    {
	String workdir  = config.cf_get_str(ConfigNames.SP_WorkingDir);
	String env      = config.getPropertyString("autoconf-env-make");
	String makefile = config.getPropertyString("autoconf-makefile");

	String rule = config.getPropertyString("autoconf-rule-make","");

	if (!(new File(makefile).exists()))
	    throw new EBuildFailed("configure didnt generate makefile "+makefile);

	String cmdline = "cd "+workdir+" && "+env+" make -f "+makefile+" "+rule;
	
	if (!exec_step("build", cmdline))
	    throw new EBuildFailed("make step failed");
    }
}
