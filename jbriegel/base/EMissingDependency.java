
package org.de.metux.briegel.base;

public class EMissingDependency extends EBriegelError
{ 	
    public EMissingDependency(String port) 
    { 
	super(port); 
    }	
    public EMissingDependency(String port, Exception e)
    {
	super(port,e);
    }
}
