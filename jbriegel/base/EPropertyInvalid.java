
package org.de.metux.briegel.base;

public class EPropertyInvalid extends EMisconfig
{ 
    public EPropertyInvalid(String property,Throwable t)
    {
	super(property,t);
    }
}
