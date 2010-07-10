
package org.de.metux.briegel.cmd;

import org.de.metux.briegel.base.EBriegelError;
import org.de.metux.briegel.conf.IConfig;
import org.de.metux.briegel.robots.CreateSystemImage;

/* this command creates a whole system image */

public class create_image extends CommandBase
{
    public create_image()
    {
	super("create_image");
    }

    public void cmd_main(String argv[]) throws EBriegelError
    {
	IConfig cf = getGlobalConfig();
	cf.cf_set("@@port-name","<MAIN>");
	new CreateSystemImage(cf).run();
    }

    public static void main(String argv[])
    {
	new create_image().run(argv);
    }
}
