//
// Stage: InstallBinpkg 
// 
// Installs binary package for given port/buildconf
//

package org.de.metux.briegel.stages;

import java.io.File;

import org.de.metux.util.StrReplace;
import org.de.metux.util.rm;

import org.de.metux.briegel.conf.IConfig;
import org.de.metux.briegel.conf.PkgLocation;
import org.de.metux.briegel.conf.ConfigNames;

import org.de.metux.briegel.base.EInstallBinpkgFailed;
import org.de.metux.briegel.base.EMisconfig;
import org.de.metux.briegel.base.EMissingBinpkg;
import org.de.metux.briegel.base.EPropertyInvalid;
import org.de.metux.briegel.base.EPropertyMissing;

import org.de.metux.briegel.base.EInstallBinpkgFailed;

public class InstallBinpkg extends Stage
{
    public InstallBinpkg(IConfig cf)
	throws EPropertyMissing, EPropertyInvalid
    {
	super("INSTALL-BINPKG", cf);
    }

    // fetch binary archive name 
    private void uncompress_archive(String workdir) 
	throws EMisconfig, EMissingBinpkg, EInstallBinpkgFailed
    {
	String filename = 
	    config.getPropertyString(PkgLocation.cfkey_binpkg_filename);

	debug("archive filename: "+filename);

	if (filename.length()<3)
	{
	    error("missing proper config value for: "+
		PkgLocation.cfkey_binpkg_filename);
	    throw new EMisconfig(PkgLocation.cfkey_binpkg_filename);
	}

	if (!(new File(filename).exists()))
	{
	    error("missing package file: "+filename);
	    throw new EMissingBinpkg(filename);
	}

	if (!decompress_tar_bz2(filename,workdir))
	    throw new EInstallBinpkgFailed(filename);
    }

    private String init_workdir() throws EMisconfig
    {
	String archive_root = 
	    StrReplace.replace("//","/",config.getPropertyString("packaging-basedir"));

	if (archive_root.length()<5)
	    throw new EMisconfig("packaging-basedir");

	if (config.cf_get_boolean("packaging-clean-basedir"))
	{
	    /* sanity check */
	    if (archive_root.length() < 10)
		throw new EMisconfig("CLEAN: archive_root seems to be too short: please check: "+archive_root);

	    rm.remove_recursive(archive_root);
	}

	mkdir(archive_root);

	debug("binpkg archive_root: "+archive_root);
	return archive_root;
    }

    void install_copy() throws EMisconfig, EInstallBinpkgFailed
    {
	notice("copying image to system ...");

	String sys_install_root = config.getPropertyString("system-install-root");
	String sys_meta_root    = config.getPropertyString("system-meta-root");
	String pkg_install_root = config.getPropertyString("packaging-install-root");
	String pkg_meta_root    = config.getPropertyString("packaging-meta-root");
	String install_root     = config.cf_get_str_mandatory(ConfigNames.SP_InstallRoot);

	debug("packaging-install-root="+pkg_install_root);
	debug("system-install-root="+sys_install_root);
	debug(ConfigNames.SP_InstallRoot+"="+install_root);

	if (pkg_meta_root.equals(sys_meta_root))
	{
	    error("$(system-meta-root)==$(packaging-meta-root) --> something went wrong!");
	    throw new EMisconfig("$(system-meta-root)==$(packaging-meta-root)");
	}
	
	if (pkg_install_root.equals(sys_install_root))
	{
	    error("$(system-install-root)==$(packaging-install-root) --> something went wrong!");
	    throw new EMisconfig("$(system-install-root)==$(packaging-install-root)");
	}
			    
	if (!copytree(pkg_install_root, sys_install_root))
	    throw new EInstallBinpkgFailed("cannot copytree() install-data");
	
	if (!copytree(pkg_meta_root,    sys_meta_root))
	    throw new EInstallBinpkgFailed("cannot copytree() meta-root");
    }

    public void run_stage() throws EMisconfig, EMissingBinpkg, EInstallBinpkgFailed
    {
	String port = config.getPropertyString(ConfigNames.SP_PortName);
	uncompress_archive(init_workdir());
	install_copy();
    }
}
