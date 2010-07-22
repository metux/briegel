
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
	IConfig cf = null;

	if (argv.length==0)
	{
	    System.err.println("build: variable name");
	    System.exit(1);
	}

	silent = true;

	String get_variable = null;

	for (int x=0; x<argv.length; x++)
	{
	    if (argv[x].equals("--debug"))
	    {
		System.err.println("Debug mode");
		silent = false;
	    }
	    else if (argv[x].equals("--port"))
	    {
		x++;
		cf = getPortConfig(argv[x]);
	    }
	    else
	    {
		get_variable = argv[x];
	    }
	}

	if (cf == null)
	    cf = getGlobalConfig();

	if (get_variable == null)
	{
	    System.err.println("Missing variable name");
	    System.exit(3);
	}

	String res;
	
	try
	{
	    res = cf.cf_get_str(get_variable);
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
