
package org.de.metux.briegel.stages;

import org.de.metux.util.StoreFile;

import org.de.metux.briegel.conf.IConfig;
import org.de.metux.briegel.conf.ConfigNames;
import org.de.metux.briegel.base.EPostInstallFailed;
import org.de.metux.briegel.base.EMisconfig;
import org.de.metux.briegel.base.EPropertyInvalid;
import org.de.metux.briegel.base.EPropertyMissing;

public class PostInstall extends Stage
{
    public PostInstall(IConfig cf)
	throws EPropertyInvalid, EPropertyMissing
    {
	super("AC-POSTINSTALL", cf);
    }

    /* FIXME: not implemented yet */
    public void compress_manpages()
    {
    }
    
    public void fixup_pkgconfig()
	throws EMisconfig, EPostInstallFailed
    {
	String cmd     = config.getPropertyString("postinstall-exec","");
	String workdir = config.getPropertyString(ConfigNames.SP_WorkingDir);

	if (cmd.length()==0)
	    return;

	if (!exec_step("postinstall", "cd "+workdir+" && "+cmd))
	    throw new EPostInstallFailed(
		config.getPropertyString(ConfigNames.SP_PortName));
    }
        
    public void run_stage() throws EMisconfig, EPostInstallFailed
    {
	fixup_pkgconfig();
	compress_manpages();
    }
}
