
package org.de.metux.briegel.cmd;

import org.de.metux.briegel.base.EBriegelError;
import org.de.metux.briegel.stages.SetupSysroot;

/* this command fetches the source of a given port */
public class setup_sysroot
{
    public static void main(String argv[]) throws EBriegelError
    {
	Init init = new Init();
	
	if (argv.length==0)
	{
	    System.err.println("setup-sysroot: missing port name");
	    return;
	}

	new SetupSysroot(init.LoadGlobal()).run();
    }
}
