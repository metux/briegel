
package org.de.metux.briegel.cmd;

import org.de.metux.briegel.base.EBriegelError;
import org.de.metux.briegel.conf.IConfig;
import org.de.metux.briegel.robots.CreateSystemImage;

/* this command fetches the source of a given port */
public class create_image
{
    public static void main(String argv[]) throws EBriegelError
    {
	Init init = new Init();
	IConfig cf = init.LoadGlobal();
	cf.cf_set("@@port-name","<MAIN>");
	new CreateSystemImage(cf).run();
    }
}
