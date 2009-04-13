//
// Stage: CreateBinpkg 
// 

package org.de.metux.briegel.stages;

import java.io.File;

import org.de.metux.util.mkdir;

import org.de.metux.briegel.base.EPropertyMissing;
import org.de.metux.briegel.base.EPropertyInvalid;
import org.de.metux.briegel.base.EMissingBinpkg;
import org.de.metux.briegel.base.EInstallBinpkgFailed;
import org.de.metux.briegel.base.EMisconfig;

import org.de.metux.briegel.conf.ConfigNames;
import org.de.metux.briegel.conf.IConfig;

import org.de.metux.briegel.base.EInstallBinpkgFailed;

public class CreateBinpkg extends Stage
{
    public CreateBinpkg(IConfig cf)
	throws EPropertyMissing, EPropertyInvalid
    {
	super("CREATE-BINPKG", cf);
    }

    public void run_stage() throws EMisconfig, EMissingBinpkg, EInstallBinpkgFailed
    {
	String port = config.getPropertyString(ConfigNames.SP_PortName);

	String pkg_base  = config.getPropertyString("packaging-basedir");
	String archive   = config.getPropertyString("packaging-archive-file");
	String meta_root = config.getPropertyString("packaging-meta-root");
	String cmd      = "";
	mkdir.mkdir_recursive(meta_root);
	config.store(meta_root+"/config.dump");

	String archive_dir = new File(archive).getParent();

	mkdir.mkdir_recursive(archive_dir);
	
	String exclude_list[] = cf_list("packaging-exclude");
	for (int x=0; x<exclude_list.length; x++)
	    cmd += " --exclude \""+exclude_list[x]+"\"";
	
	cmd = "cd "+pkg_base+" && tar "+cmd+" -cjf "+archive+" *";
	exec(cmd);
    }
}
