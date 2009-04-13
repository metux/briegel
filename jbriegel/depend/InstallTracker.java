/*
    InstallTracker	[Tracker]
    
    Tracks the right order of packages to be installed, based on 
    "require-build" and "import" dependencies.
*/

package org.de.metux.briegel.depend;

import org.de.metux.briegel.conf.PkgLocation;
import org.de.metux.briegel.conf.IConfig;

public class InstallTracker extends Tracker
{
    String def_dep_fields[] = { "require-build", "import" };
    
    public InstallTracker(IConfig conf)
    {
	super(conf);
	dep_fields = def_dep_fields;
    }
    
    public boolean check_package(IConfig cf)
    {
	return PkgLocation.check_installed(cf);
    }
}
