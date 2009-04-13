
package org.de.metux.briegel.cmd;

import org.de.metux.briegel.base.EBriegelError;
import org.de.metux.briegel.base.EPropertyInvalid;
import org.de.metux.briegel.conf.IConfig;

/* this command fetches the source of a given port */
public class cf_query
{
    public static void main(String argv[]) throws EBriegelError
    {
	IConfig globalcf;
	Init init = new Init();
	
	if (argv.length==0)
	{
	    System.err.println("build: variable name");
	    System.exit(1);
	}

	
	if (argv[0].equals("--debug"))
	{
	    argv[0] = argv[1];
	    globalcf = init.LoadGlobal();
	}
	else
	{
	    init.silent = true;
	    globalcf = init.LoadGlobal();
	}

	String res;
	
	try
	{
	    res = globalcf.cf_get_str(argv[0]);
	}
	catch (EPropertyInvalid e)
	{
	    System.err.println(e);
	    System.exit(2);
	    return;
	}

	System.out.print(res);
    }
}
