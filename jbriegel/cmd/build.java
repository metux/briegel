
package org.de.metux.briegel.cmd;

import org.de.metux.briegel.base.EBriegelError;
import org.de.metux.briegel.robots.RecursiveBuild;

/* this command fetches the source of a given port */
public class build
{
    public static void main(String argv[]) throws EBriegelError
    {
	Init init = new Init();
	
	if (argv.length==0)
	{
	    System.err.println("build: missing port name");
	    return;
	}

	RecursiveBuild bot = new RecursiveBuild(argv[0],init.LoadPort(argv[0]));
	bot.run();
    }
}
