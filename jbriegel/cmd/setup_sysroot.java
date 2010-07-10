
package org.de.metux.briegel.cmd;

import org.de.metux.briegel.base.EBriegelError;
import org.de.metux.briegel.stages.SetupSysroot;

/* this command sets up the sysroot image for toolchain */

public class setup_sysroot extends CommandBase
{
    public setup_sysroot()
    {
	super("setup_sysroot");
    }

    public void cmd_main(String argv[]) throws EBriegelError
    {
	if (argv.length==0)
	{
	    System.err.println("setup-sysroot: missing port name");
	    return;
	}

	new SetupSysroot(getGlobalConfig()).run();
    }

    public static void main(String argv[])
    {
	new setup_sysroot().run(argv);
    }
}
