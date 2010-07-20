
package org.de.metux.briegel.builder_autoconf;

import java.io.File;
import org.de.metux.util.StoreFile;
import org.de.metux.briegel.stages.Stage;
import org.de.metux.briegel.base.EPropertyMissing;
import org.de.metux.briegel.base.EPropertyInvalid;
import org.de.metux.briegel.base.EMisconfig;
import org.de.metux.briegel.base.EBuildFailed;
import org.de.metux.briegel.conf.IConfig;

public class Build extends Stage
{
    public Build(IConfig cf)
	throws EPropertyMissing, EPropertyInvalid
    {
	super("AC-BUILD", cf);
    }
    
    public void run_stage() throws EMisconfig, EBuildFailed
    {
	String workdir  = config.getPropertyString("@@workdir");
	String env      = config.getPropertyString("autoconf-env-make");
	String makefile = config.getPropertyString("autoconf-makefile");

	String rule = config.getPropertyString("autoconf-rule-make","");

	if (!(new File(makefile).exists()))
	    throw new EBuildFailed("configure didnt generate makefile "+makefile);

	String cmdline = "cd "+workdir+" && "+env+" make -f "+makefile+" "+rule;
	
	if (!exec(
	    cmdline+"; export retval=$? ; echo ; exit $retval",
	    workdir+"/BRIEGEL-log-build",
	    workdir+"/BRIEGEL-cmd-build"
	))
	    throw new EBuildFailed("make step failed");
    }
}
