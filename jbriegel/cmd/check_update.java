
package org.de.metux.briegel.cmd;

import org.de.metux.util.VersionStack;

import org.de.metux.briegel.base.EBriegelError;
import org.de.metux.briegel.conf.IConfig;
import org.de.metux.briegel.conf.BriegelConf;
import org.de.metux.briegel.stages.FetchSource;


/* this checks for new versions of given package/port */

public class check_update extends CommandBase
{
    public check_update()
    {
	super("check_update");
    }

    public void cmd_main(String argv[]) throws EBriegelError
    {
	silent = true;
	
	if (argv.length==0)
	{
	    /* using the world file to get the packag list */
	    IConfig globalconf = getGlobalConfig();
	    argv = globalconf.getWorldList();
	}

	for (int x=0; x<argv.length; x++)
	{
	    IConfig port = getPortConfig(argv[x]);

	    if (!port.cf_get_boolean(BriegelConf.k_csdb_query_versions,false))
		System.err.println("# "+
		    port.getPortName()+" not connected to CSDB");

	    VersionStack stk = port.getNewerVersions();
	    if (stk.size()>0)
		System.out.println(argv[x]+": "+stk);
	}
    }

    public static void main(String argv[])
    {
	new check_update().run(argv);
    }
}
