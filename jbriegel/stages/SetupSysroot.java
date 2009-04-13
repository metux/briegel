
package org.de.metux.briegel.stages;

import java.io.File;

import org.de.metux.briegel.base.EMisconfig;
import org.de.metux.briegel.base.ECannotSetupSysroot;
import org.de.metux.briegel.base.EMissingSysrootMarker;
import org.de.metux.briegel.base.EMissingSysrootImage;
import org.de.metux.briegel.base.EPropertyInvalid;
import org.de.metux.briegel.base.EPropertyMissing;

import org.de.metux.briegel.conf.IConfig;
import org.de.metux.briegel.conf.ConfigNames;

public class SetupSysroot extends Stage
{
    static int sysroot_minsize = 10;
    
    public SetupSysroot(IConfig cf)
	throws EPropertyMissing, EPropertyInvalid
    {
	super("SETUP-SYSROOT",cf);
    }

    public void run_stage()	
	throws EMisconfig, ECannotSetupSysroot
    {
	String sysroot = config.getPropertyString("system-root");
	String image   = config.getPropertyString("sysroot-image");

	// Sanity check 1: test for plausibility
	if (sysroot.length()<sysroot_minsize)
	{
	    error("sanity check failed: sysroot too short (min. "+sysroot_minsize+" chars): \""+sysroot+"\"");
	    error("this is for your own protected, to prevent briegel killing your system");
	    error("if this sysroot is really right, please choose a longer name and (sym)link to it");	    
	    throw new EMisconfig(config.getPropertyString(ConfigNames.SP_PortName));
	}

	// Sanity check 2: test whether directory is marked as sysroot
	if (!(new File(sysroot+"/.SYSROOT").exists()))
	    throw new EMissingSysrootMarker(sysroot);
	
	// Sanity check 3: test for image file
	if (!(new File(image).exists()))
	    throw new EMissingSysrootImage(image);
	
	// rm.remove_recursive() geht hier wg. des symlinks nicht
	String command = "cd "+sysroot+" && [ -f .SYSROOT ] && rm -Rf * && tar -xjf "+image+" && touch .SYSROOT";
	exec(command);	
    }
}
