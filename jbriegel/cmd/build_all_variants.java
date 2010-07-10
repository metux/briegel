
package org.de.metux.briegel.cmd;

import org.de.metux.briegel.base.EBriegelError;
import org.de.metux.briegel.robots.BulkBuild;

/* this command builds all variants of a given port */

public class build_all_variants extends CommandBase
{
    public build_all_variants()
    {
	super("build_all_variants");
    }

    public void cmd_main(String argv[]) throws EBriegelError
    {
	if (argv.length==0)
	{
	    System.err.println("build: missing port name");
	    return;
	}

	BulkBuild bot = new BulkBuild(argv[0],getPortConfig(argv[0]));
	bot.run();
    }

    public static void main(String argv[]) throws EBriegelError
    {
	new build_all_variants().run(argv);
    }
}
