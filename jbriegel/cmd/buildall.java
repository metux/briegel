
package org.de.metux.briegel.cmd;

import org.de.metux.briegel.base.EBriegelError;
import org.de.metux.briegel.base.EPropertyInvalid;
import org.de.metux.briegel.robots.BuildAll;
import org.de.metux.briegel.conf.IConfig;

/* this command fetches the source of a given port */
public class buildall
{
    public static void main(String argv[]) throws EBriegelError
    {
	Init init = new Init();
	IConfig cf = init.LoadGlobal();
	BuildAll bot = new BuildAll(cf);
	bot.run();
    }
}
