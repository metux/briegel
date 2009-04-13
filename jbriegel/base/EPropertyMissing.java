
package org.de.metux.briegel.base;

public class EPropertyMissing extends EMisconfig
{ 
    public EPropertyMissing(String port,String property) 
    { 
	super("Missing property "+property+"in port "+port); 
    }

    public EPropertyMissing(String property)
    {
	super(property);
    }
    
    public EPropertyMissing(String property,Throwable t)
    {
	super(property,t);
    }
}
