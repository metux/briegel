
package org.de.metux.briegel.cmd;

import org.de.metux.briegel.base.EBriegelError;
import org.de.metux.briegel.stages.FetchSource;

/* this command fetches the source of a given port */
public class fetch_source extends CommandBase
{
    public fetch_source()
    {
	super("fetch_source");
    }

    public void cmd_main(String argv[]) throws EBriegelError
    {
	if (argv.length==0)
	{
	    System.err.println("build: missing port name");
	    return;
	}

	new FetchSource(getPortConfig(argv[0])).run();
    }

    public static void main(String argv[])
    {
	new fetch_source().run(argv);
    }
}
