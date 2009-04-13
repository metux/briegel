
package org.de.metux.briegel.depend;

import java.util.Hashtable;

import org.de.metux.util.Version;
import org.de.metux.util.Depend;

import org.de.metux.briegel.base.EMisconfig;
import org.de.metux.briegel.base.EPropertyMissing;
import org.de.metux.briegel.base.EPropertyInvalid;
import org.de.metux.briegel.base.EMissingPort;

import org.de.metux.briegel.conf.IConfig;

public abstract class Tracker
{
    public Tree tree;
    public IConfig master_conf;
    public String[] dep_fields = { "***" };

    public Tracker(IConfig conf)
    {
	master_conf = conf;
	tree = new Tree();
    }
    
    public void track_port(IConfig port)
	throws EMissingPort, EPropertyInvalid, EPropertyMissing, EMisconfig
    {
	Node n = tree.add(port.getPortName(),port);
	track_node(n);
    }

    private void track_node(Node n)
	throws EMissingPort, EPropertyInvalid, EPropertyMissing, EMisconfig
    {
	for (int a=0; a<dep_fields.length; a++)
	{
	    String depend[] = n.conf.cf_get_list(dep_fields[a]);
	    for (int x=0; x<depend.length; x++)
	    {
		Depend dep = new Depend(depend[x]);
	        track_port(dep.package_name);
	        tree.add_dependency(n.name,dep.package_name);
	    }
	}
    }
	        
    public void track_port(String port) 
	throws EMissingPort, EPropertyInvalid, EPropertyMissing, EMisconfig
    {
	Node n = prepare_port(port);
	for (int a=0; a<dep_fields.length; a++)
	{
	    String depend[] = n.conf.cf_get_list(dep_fields[a]);
	    for (int x=0; x<depend.length; x++)
	    {
		Depend dep = new Depend(depend[x]);
	        track_port(dep.package_name);
	        tree.add_dependency(port,dep.package_name);
	    }
	}
    }
    
    public void track_port(String ports[]) 
	throws EMissingPort, EPropertyInvalid, EPropertyMissing, EMisconfig
    {
	for (int x=0; x<ports.length; x++)
	    track_port(ports[x]);
    }
    
    private Node prepare_port(IConfig port)
	throws EMissingPort, EPropertyInvalid, EPropertyMissing, EMisconfig
    {
	return tree.add(port.getPortName(),port);
    }
    
    private Node prepare_port(String port) 
	throws EMissingPort, EPropertyInvalid, EPropertyMissing, EMisconfig
    {
	Node n = tree.get(port);
	if (n!=null)
	    return n;
	
	IConfig cf = master_conf.allocLoadPort(port);

	return tree.add(port,cf);
    }

    abstract public boolean check_package(IConfig cf)
	throws EPropertyInvalid, EPropertyMissing;
    
    // clean existing ports
    public boolean clean_existing()
	throws EPropertyInvalid, EPropertyMissing
    {
	boolean gotone = false;
	Hashtable candidates = tree.getLeafs();

	NodeIterator i = new NodeIterator(candidates);
	while (i.next())
	    if (check_package(i.current.conf))
	    {
		tree.remove(i.current.name);
		gotone = true;
	    }

	return gotone;
    }
    
    public void remove(String port)
    {
	tree.remove(port);
    }
    
    public NodeIterator getCandidates()
	throws EPropertyInvalid, EPropertyMissing
    {
	while (clean_existing());
	return new NodeIterator ( tree.getLeafs() );    
    }
}
