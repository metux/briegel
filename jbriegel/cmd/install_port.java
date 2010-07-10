
package org.de.metux.briegel.cmd;

import org.de.metux.briegel.base.EBriegelError;
import org.de.metux.briegel.conf.IConfig;
import org.de.metux.briegel.robots.RecursiveBuild;
import org.de.metux.briegel.robots.InstallRuntimeBinpkg;

/* this command installs an port into the target image */

public class install_port extends CommandBase
{
    public install_port()
    {
	super("install_port");
    }

    public void cmd_main(String argv[]) throws EBriegelError
    {
	if (argv.length==0)
	{
	    System.err.println("install_port <port>");
	    System.exit(exitcode_err_missing_port);
	}

	IConfig cf = getPortConfig(argv[0]);
	new RecursiveBuild(cf).run();
	new InstallRuntimeBinpkg(cf).run();
    }

    public static void main(String argv[])
    {
	new install_port().run(argv);
    }
}
