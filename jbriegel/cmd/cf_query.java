
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
		argv[x] = null;
	    }
	}

	cf = getGlobalConfig();

	for (int x=0; x<argv.length; x++)
	{
	    if ((argv[x] == null) || (argv[x].equals("--debug")));
	    else if (argv[x].equals("--package"))
	    {
		x++;
		cf = getPackageConfig(argv[x]);
	    }
	    else if (argv[x].equals("--port"))
	    {
		x++;
		cf = getPortConfig(argv[x]);
	    }
	    else if (argv[x].equals("--set"))
	    {
		cf.cf_set(argv[x+1],argv[x+2]);
		x+=2;
	    }
	    else
	    {
		try
		{
		    String res = cf.cf_get_str(argv[x]);
		    System.out.println(res);
		}
		catch (EPropertyInvalid e)
		{
		    System.err.println(e);
		    System.exit(2);
		    return;
		}
	    }
	}
    }

    public static void main(String argv[])
    {
	new cf_query().run(argv);
    }
}
