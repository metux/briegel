/*
    BuildTracker	[Tracker]
    
    Tracks the right order of packages to be built, based on 
    "require-build" and "import" dependencies.
*/

package org.de.metux.briegel.depend;

import org.de.metux.briegel.conf.PkgLocation;
import org.de.metux.briegel.conf.IConfig;

import org.de.metux.briegel.base.EPropertyInvalid;
import org.de.metux.briegel.base.EPropertyMissing;

public class BuildTracker extends Tracker
{
    String def_dep_fields[] = { "require-build", "import" };
    
    public BuildTracker(IConfig conf)
    {
	super(conf);
	dep_fields = def_dep_fields;
    }
    
    public boolean check_package(IConfig cf)
	throws EPropertyInvalid, EPropertyMissing
    {
	return PkgLocation.check_binary_archive(cf);
    }
}
