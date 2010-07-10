
package org.de.metux.briegel.cmd;

import org.de.metux.briegel.base.EBriegelError;
import org.de.metux.briegel.base.EPropertyInvalid;
import org.de.metux.briegel.conf.IConfig;

/* this command fetches the source of a given port */
public class cf_query extends CommandBase
{
    public cf_query()
    {
	super("cf_query");
    }

    public void cmd_main(String argv[]) throws EBriegelError
    {
	IConfig globalcf;
	
	if (argv.length==0)
	{
	    System.err.println("build: variable name");
	    System.exit(1);
	}

	if (argv[0].equals("--debug"))
	{
	    argv[0] = argv[1];
	    globalcf = getGlobalConfig();
	}
	else
	{
	    silent = true;
	    globalcf = getGlobalConfig();
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

    public static void main(String argv[])
    {
	new cf_query().run(argv);
    }
}
