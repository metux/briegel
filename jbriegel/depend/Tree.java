
package org.de.metux.briegel.depend;

import java.util.Hashtable;
import java.util.Enumeration;

import org.de.metux.briegel.conf.IConfig;

public class Tree
{
    Hashtable nodes;

    Tree()
    {
	nodes = new Hashtable();
    }
    
    public NodeIterator enumerate()
    {
	return new NodeIterator(nodes);
    }

    Node add(String name, IConfig pkg)
    {
	Node n = new Node(name,pkg);
	nodes.put(n.name,n);
	return n;
    }

    Node get(String name)
    {
	Node n;
	return (Node)nodes.get(name);
    }

    void add_dependency(String parent, String depend)
    {
	Node node_parent = get(parent);
	Node node_depend = get(depend);
	
	if (node_parent==null)
	    throw new RuntimeException(
		"add_dependency: could not get parent node \""+parent+"\"");

	if (node_depend==null)
	    throw new RuntimeException(
		"add_dependency: could not get depend node \""+parent+"\"");
		
	node_parent.dependencies.put(node_depend.name, node_depend);
	node_depend.references.put(node_parent.name, node_parent);
    }

    public String toString()
    {
	String list = "";
	for (Enumeration enum_nodes = nodes.keys(); 
	     enum_nodes.hasMoreElements();)
	{
	    String port = (String)enum_nodes.nextElement();
	    list += port+":";
	    Node node = (Node)nodes.get(port);
	    for (Enumeration enum_deps = node.dependencies.keys(); 
		 enum_deps.hasMoreElements(); 
		 list += " "+(String)enum_deps.nextElement()
	    );
	    list += "\n";
	}
	
	return list;
    }
    
    // fetch an node which has no dependencies
    public Hashtable getLeafs()
    {
	Hashtable found = new Hashtable();
	NodeIterator i = new NodeIterator (nodes);
	while (i.next())
	{
	    if (i.current.dependencies.size()<1)
		found.put(i.current.name,i.current);
	}

	return found;
    }

    // clear given port from dependency tree
    // FIXME: move this to the tree class
    public void remove(String name)
    {
	// FIXME: we could use tree.enumerateReferences - so we get
	// just those nodes which reference our given port
	// so we walk through *all* nodes.
	NodeIterator i = enumerate();
	while (i.next())
	    i.current.clear_depend(name);

	nodes.remove(name);
    }
}
