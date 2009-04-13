
package org.de.metux.briegel.base;

public class EBriegelError extends Exception
{
    public EBriegelError(String s)
    {
	super(s);
    }
    public EBriegelError(String s, Throwable e)
    {
	super(s,e);
    }
}
