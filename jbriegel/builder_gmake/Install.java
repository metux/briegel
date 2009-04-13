
package org.de.metux.briegel.builder_gmake;

import org.de.metux.util.StoreFile;
import org.de.metux.util.StrUtil;
import org.de.metux.briegel.stages.Stage;
import org.de.metux.briegel.conf.IConfig;
import org.de.metux.briegel.base.EPropertyMissing;
import org.de.metux.briegel.base.EPropertyInvalid;
import org.de.metux.briegel.base.EInstallFailed;
import org.de.metux.briegel.base.EMisconfig;

public class Install extends Stage
{
    public Install(IConfig cf)
	throws EPropertyMissing, EPropertyInvalid
    {
	super("GMAKE-INSTALL", cf);
    }
    
    public void run_stage() throws EMisconfig, EInstallFailed
    {
	String workdir = config.getPropertyString("@@workdir");
	String env     = config.getPropertyString("gmake-env-install");
	String fn      = workdir+"/BRIEGEL-cmd-install";
	String cmd     = 
	    "cd "+workdir+" && "+env+" "+
	    config.getPropertyString("gmake-exec-install");
	
	if (!StoreFile.store(fn, cmd, "ugo+rx"))
	    throw new EInstallFailed("could not store cmd file: "+fn);
	    
	if (!exec(fn))
	    throw new EInstallFailed(config.getPropertyString("@@port-name"));
    }
}
