
package org.de.metux.briegel.builder_autoconf;

import org.de.metux.briegel.base.EPropertyMissing;
import org.de.metux.briegel.base.EPropertyInvalid;
import org.de.metux.briegel.base.EInstallFailed;
import org.de.metux.briegel.base.EMisconfig;
import org.de.metux.briegel.conf.IConfig;
import org.de.metux.briegel.stages.Stage;

public class Install extends Stage
{
    public Install(IConfig cf)
	throws EPropertyMissing, EPropertyInvalid
    {
	super("AC-INSTALL", cf);
    }
    
    public void run_stage() throws EMisconfig, EInstallFailed
    {
	String par = 
	((config.cf_get_boolean("install-strip") &&
	 config.cf_get_boolean("builder-supports-install-strip")) ?
	 "autoconf-rule-install-strip" : "autoconf-rule-install" );

	String rule = config.getPropertyString(par);

	String workdir = config.getPropertyString("@@workdir");
	String instpar = config.getPropertyString("autoconf-install-param");
	String env     = config.getPropertyString("autoconf-env-install");
	String mk      = config.getPropertyString("autoconf-makefile");

	String cmdline = 
	    "cd "+workdir+" && "+env+" make -f "+mk+" "+rule+" "+instpar;

	if (!exec(
	    cmdline,
	    workdir+"/BRIEGEL-log-install",
	    workdir+"/BRIEGEL-cmd-install"
	))
	    throw new EInstallFailed(config.getPropertyString("@@port-name"));
    }
}
