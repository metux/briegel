
package org.de.metux.briegel.conf;

import java.io.File;
import java.util.Hashtable;

import org.de.metux.util.Version;

import org.de.metux.briegel.base.EPropertyInvalid;
import org.de.metux.briegel.base.EPropertyMissing;

public class PkgLocation
{
    public static final String cfkey_binpkg_filename = "packaging-archive-file";

    public static boolean check_binary_archive(IConfig cf)
	throws EPropertyInvalid, EPropertyMissing
    {
	String filename = cf.cf_get_str_mandatory(cfkey_binpkg_filename);
	return ((new File(filename)).exists());
    }

    public static boolean check_installed(IConfig cf)
    {
	cf.warning("PkgLocation::check_installed() dummy");
	return false;    
    }    
}
