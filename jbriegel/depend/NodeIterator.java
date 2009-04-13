
package org.de.metux.briegel.depend;

import java.util.Hashtable;
import java.util.Enumeration;

public class NodeIterator
{
    Enumeration enumerator;
    public Hashtable nodes;
    public Node current;
        
    public NodeIterator(Hashtable ht)
    {
	nodes = ht;
	enumerator = nodes.elements();
    }

    public boolean isEmpty()
    {
	return nodes.isEmpty();
    }
    
    public void reset()
    {
	current = null;
	enumerator = nodes.elements();
    }
        
    public Node nextNode()
    {
	if (!enumerator.hasMoreElements())
	    return null;
	    
	current = (Node)enumerator.nextElement();
	return current;
    }
    
    public boolean next()
    {
	return (nextNode()!=null);
    }
    
    public boolean remove()
    {
	if (current==null)
	    return false;
	
	nodes.remove(current.name);
	return true;
    }
}
