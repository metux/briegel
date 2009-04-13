
package org.de.metux.briegel.depend;

import java.util.Hashtable;
import org.de.metux.util.Version;
import org.de.metux.briegel.conf.IConfig;

public class Node
{
    public IConfig conf;
    public String name;
    public Hashtable dependencies;
    public Hashtable references;
    public Version version;

    public Node(String new_name, IConfig new_conf)
    {
	dependencies = new Hashtable();
	references   = new Hashtable();
	name = new_name;
	conf = new_conf;
    }
    
    public Node get_depend(String name)
    {
	return (Node)dependencies.get(name);
    }

    public Node get_ref(String name)
    {
	return (Node)references.get(name);
    }
    
    // remove given node from our dependency list
    public void clear_depend(String name)
    {
	dependencies.remove(name);
    }    
    public void clear_depend(Node node)
    {
	dependencies.remove(node.name);
    }
    
    public void clear_ref(String name)
    {
	references.remove(name);
    }
}
