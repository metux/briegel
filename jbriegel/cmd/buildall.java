
package org.de.metux.briegel.cmd;

import org.de.metux.briegel.base.EBriegelError;
import org.de.metux.briegel.base.EPropertyInvalid;
import org.de.metux.briegel.robots.BuildAll;
import org.de.metux.briegel.conf.IConfig;

/* this command builds all ports in world file */

public class buildall extends CommandBase
{
    public buildall()
    {
	super("buildall");
    }

    public void cmd_main(String argv[]) throws EBriegelError
    {
	BuildAll bot = new BuildAll(getGlobalConfig());
	bot.run();
    }

    public static void main(String argv[])
    {
	new buildall().run(argv);
    }
}
