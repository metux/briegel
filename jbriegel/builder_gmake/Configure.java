
package org.de.metux.briegel.builder_gmake;

import java.io.File;

import org.de.metux.util.StoreFile;
import org.de.metux.util.StrUtil;

import org.de.metux.briegel.base.EPropertyInvalid;
import org.de.metux.briegel.base.EPropertyMissing;
import org.de.metux.briegel.base.EMisconfig;
import org.de.metux.briegel.base.EBuildFailed;
import org.de.metux.briegel.conf.IConfig;
import org.de.metux.briegel.stages.Stage;
import org.de.metux.briegel.stages.IBuilderRun;

public class Configure extends Stage
{
    public Configure(IConfig cf)
	throws EPropertyInvalid, EPropertyMissing
    {
	super("GMAKE-CONF",cf);
    }
    
    public void run_stage() throws EMisconfig, EBuildFailed
    {
	String workdir   = config.getPropertyString("@@workdir");
	String conf_exec = config.getPropertyString("gmake-exec-configure", null);
	String conf_env  = config.getPropertyString("gmake-env-configure", null);

	if (!StrUtil.isEmpty(conf_exec))
	{
	    String cmdline = "cd "+workdir+" && "+conf_env+" "+conf_exec;
	
	    if (!StoreFile.store(workdir+"/BRIEGEL-cmd-configure", cmdline, "ugo+rx"))
	    	throw new EBuildFailed("could not store cmd file: ");
	
	    if (!exec(cmdline))
		throw new EBuildFailed("gmake step failed");
	}
    }
}
