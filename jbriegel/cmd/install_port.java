
package org.de.metux.briegel.cmd;

import org.de.metux.briegel.base.EBriegelError;
import org.de.metux.briegel.conf.IConfig;
import org.de.metux.briegel.robots.RecursiveBuild;
import org.de.metux.briegel.robots.InstallRuntimeBinpkg;

/* this command fetches the source of a given port */
public class install_port
{
    public static void main(String argv[]) throws EBriegelError
    {
	Init init = new Init();
	if (argv.length==0)
	{
	    System.err.println("install_port <port>");
	    System.exit(1);
	}
	    
	IConfig cf = init.LoadPort(argv[0]);
	new RecursiveBuild(cf).run();
	new InstallRuntimeBinpkg(cf).run();
    }
}
