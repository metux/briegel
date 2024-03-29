
package org.de.metux.briegel.robots;

import org.de.metux.briegel.conf.IConfig;
import org.de.metux.briegel.conf.ConfigNames;
import org.de.metux.briegel.depend.Tracker;
import org.de.metux.briegel.depend.NodeIterator;
import org.de.metux.briegel.depend.InstallTracker;

import org.de.metux.briegel.stages.Stage;
import org.de.metux.briegel.stages.InstallBinpkg;

import org.de.metux.briegel.base.EBriegelError;
import org.de.metux.briegel.base.EPropertyMissing;
import org.de.metux.briegel.base.EPropertyInvalid;

public class InstallTree extends Stage
{
    String ports[];
    
    public InstallTree(IConfig my_conf, String my_port)
	throws EPropertyMissing, EPropertyInvalid
    {
	super("INSTALL-TREE",my_conf);
	ports = new String[1];
	ports[0] = my_port;
    }

    public InstallTree(IConfig my_conf, String my_ports[])
	throws EPropertyMissing, EPropertyInvalid
    {
	super("INSTALL-TREE",my_conf);
	ports = my_ports;
    }
	    
    public void install_pkg(IConfig cf)
	throws EBriegelError
    {
	/* copy pathes from parent to childs */
	cf.cf_set(ConfigNames.SP_InstallRoot, config.cf_get_str_mandatory(ConfigNames.SP_InstallRoot));
	cf.cf_set(ConfigNames.SP_MetaRoot,    config.cf_get_str_mandatory(ConfigNames.SP_MetaRoot));
	cf.cf_set("system-install-root", config.getPropertyString("system-install-root"));
	cf.cf_set("system-meta-root",    config.getPropertyString("system-meta-root"));	
	
	new InstallBinpkg(cf).run();
    }
    
    public void run_stage()
	throws EBriegelError
    {
	Tracker tracker = new InstallTracker(config);
	for (int x=0; x<ports.length; x++)
	    tracker.track_port(ports[x]);
	
	NodeIterator candidates;
	
	while (((candidates=tracker.getCandidates())!=null) && 
	        (!candidates.isEmpty()))
	{
	    while (candidates.next())
	    {
		install_pkg(candidates.current.conf);
		tracker.remove(candidates.current.name);
	    }	
	}
    }
}
