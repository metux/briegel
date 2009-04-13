
package org.de.metux.briegel.base; 

import org.de.metux.util.Environment;
import org.de.metux.log.ILogger;
import org.de.metux.log.LoggerTerm;
import org.de.metux.log.LoggerASCII;
import org.de.metux.log.LoggerDummy;

/* -- briegel logger factory -- */
public class FBriegelLogger
{
    final public static ILogger alloc(String name)
    {
	if (name==null)
	    name = "";
	
	if (name.equals("terminal") || (name.length()==0))
	    return new LoggerTerm();

	if (name.equals("ascii") || name.equals (""))
	    return new LoggerASCII();

	if (name.equals("null"))
	    return new LoggerDummy();

	System.err.println("undefined logger class \""+name+"\"");
	return null;
    }
    
    final public static ILogger alloc()
    {
	ILogger logger;
	if ((logger = alloc(getDefaultLogger()))==null)
	{
	    System.err.println ("could not allocate logger! ");
	    return null;
	}
	return logger;
    }
	
    final public static String getDefaultLogger()
    {
	String n = Environment.getenv("BRIEGEL_LOGGER");
	if (n==null) 
	    return "";
	else
	    return n;
    }
}
