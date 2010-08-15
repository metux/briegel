
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
    static final int sysroot_minsize = 10;
    String sysroot;
    String sr_git_repo;

    public SetupSysroot(IConfig cf)
	throws EPropertyMissing, EPropertyInvalid
    {
	super("SETUP-SYSROOT",cf);
    }

    /* Setup the sysroot tree via git checkout */
    private void run_git()
	throws EMissingSysrootImage, ECannotSetupSysroot
    {
	String sr_git_ref  = config.cf_get_str(ConfigNames.SystemRoot_GitRef, "");

        if (sr_git_ref.trim().length()<1)
	    throw new EMissingSysrootImage("sysroot git ref");

	String sr_git_cmd  = config.cf_get_str(ConfigNames.SystemRoot_GitCmd, "git");

	if (!exec("cd "+sysroot+" && rm -Rf *"))
	    throw new ECannotSetupSysroot("error erasing old sysroot");

	mkdir(sysroot);

	if (!exec("cd "+sysroot+" && "+sr_git_cmd+" --git-dir="+sr_git_repo+
	     " --work-tree="+sysroot+
	     " checkout -f "+sr_git_ref+" && touch .SYSROOT"))
	    throw new ECannotSetupSysroot("cannot checkout sysroot tree");
    }

    /* Setup the sysroot tree via tarball */
    private void run_tarball()
	throws EMissingSysrootMarker, EPropertyInvalid, EMissingSysrootImage,
	       EPropertyMissing, ECannotSetupSysroot
    {
	String image   = config.cf_get_str_mandatory(ConfigNames.SystemRoot_Image);

	// Sanity check 2: test whether directory is marked as sysroot
	if (!(new File(sysroot+"/.SYSROOT").exists()))
	    throw new EMissingSysrootMarker(sysroot);

	// Sanity check 3: test for image file
	if (!(new File(image).exists()))
	    throw new EMissingSysrootImage(image);

	// rm.remove_recursive() geht hier wg. des symlinks nicht
	String command = "cd "+sysroot+" && [ -f .SYSROOT ] && rm -Rf * && tar -xjf "+image+" && touch .SYSROOT";
	if (!exec(command))
	    throw new ECannotSetupSysroot("tarball uncompression failed: "+image);
    }

    public void run_stage()
	throws EMisconfig, ECannotSetupSysroot
    {
	sysroot = config.cf_get_str_mandatory(ConfigNames.SystemRoot_Directory);
	config.cf_set(ConfigNames.SP_SystemRoot, sysroot);

	// Sanity check 1: test for plausibility
	if (sysroot.length()<sysroot_minsize)
	{
	    error("sanity check failed: sysroot too short (min. "+sysroot_minsize+" chars): \""+sysroot+"\"");
	    error("this is for your own protected, to prevent briegel killing your system");
	    error("if this sysroot is really right, please choose a longer name and (sym)link to it");	    
	    throw new EMisconfig(config.getPropertyString(ConfigNames.SP_PortName));
	}

	sr_git_repo = config.cf_get_str(ConfigNames.SystemRoot_GitRepo, "");

	/* Should we use git checkout instead of a tarball ? */
	if (sr_git_repo.trim().length()>1)
	    run_git();
	else
	    run_tarball();
    }
}
