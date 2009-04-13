
package org.de.metux.briegel.robots;

import org.de.metux.briegel.stages.Stage;
import org.de.metux.briegel.stages.SetupSysroot;

import org.de.metux.briegel.base.EMissingBinpkg;
import org.de.metux.briegel.base.EMissingPort;
import org.de.metux.briegel.base.EPropertyMissing;
import org.de.metux.briegel.base.EPropertyInvalid;
import org.de.metux.briegel.base.EBriegelError;
import org.de.metux.briegel.conf.IConfig;
import org.de.metux.briegel.conf.PkgLocation;

import org.de.metux.briegel.depend.Tracker;
import org.de.metux.briegel.depend.BuildTracker;
import org.de.metux.briegel.depend.NodeIterator;

/* this command fetches the source of a given port */
public class RecursiveBuild extends Stage
{
    String port;
    
    public RecursiveBuild(String my_port, IConfig my_conf)
	throws EMissingPort, EPropertyMissing, EPropertyInvalid
    {
	super("REC-BUILD", my_conf);
	port = my_port;
	if (my_conf==null)
	    throw new EMissingPort(my_port);
    }
    	
    public RecursiveBuild(IConfig my_conf)
	throws EPropertyInvalid, EPropertyMissing
    {
	super("REC-BUILD",my_conf);
	port = config.getPortName();
    }

    public void run_stage()
	throws EBriegelError
    {
	Tracker tracker = new BuildTracker(config);
//	tracker.track_port(port);
	tracker.track_port(config);

	notice(tracker.tree.toString());

	NodeIterator candidates;
	while (((candidates=tracker.getCandidates())!=null) &&
	        (!candidates.isEmpty()))
	{
	    while (candidates.next())
	    {
		IConfig dep_conf;
		BuildBinpkg bot_binpkg;
		
		if (candidates.current.conf == null)
		    throw new RuntimeException("candidate: "+candidates.current.name+" missig config");

		dep_conf = candidates.current.conf;
//		if ((dep_conf = config.allocLoadPort(candidates.current.name))==null)
//		    throw new EMissingPort(candidates.current.name);
		
		// setup system root 
		new SetupSysroot(dep_conf).run();
		
		bot_binpkg = new BuildBinpkg(dep_conf);
		bot_binpkg.run();
		
		if (!PkgLocation.check_binary_archive(candidates.current.conf))
		    throw new EMissingBinpkg(candidates.current.name);
	    }
	}

	// debug output
	// notice(tracker.tree);
    }
}
