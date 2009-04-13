
package org.de.metux.briegel.base;

public class EBuildFailed extends EBriegelError
{ 	
    public EBuildFailed(String port) 
    { 
	super(port); 
    }	
    
    public EBuildFailed(String port, Exception e)
    {
	super(port,e);
    }
}
