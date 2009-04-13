
package org.de.metux.briegel.base;

public class EMisconfig extends EBriegelError
{ 	
    public EMisconfig(String port) 
    { 
	super(port); 
    }	
    public EMisconfig(String port, Throwable ex)
    {
	super(port,ex);
    }
}
