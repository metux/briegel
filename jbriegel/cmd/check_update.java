
package org.de.metux.briegel.cmd;

import org.de.metux.util.VersionStack;

import org.de.metux.briegel.base.EBriegelError;
import org.de.metux.briegel.conf.IConfig;
import org.de.metux.briegel.conf.BriegelConf;
import org.de.metux.briegel.stages.FetchSource;


/* this command fetches the source of a given port */
public class check_update
{
    public static void main(String argv[]) throws EBriegelError
    {
	Init init = new Init();
	init.silent = true;
	
	if (argv.length==0)
	{
	    /* using the world file to get the packag list */
	    IConfig globalconf = init.LoadGlobal();
	    argv = globalconf.getWorldList();
	}

	for (int x=0; x<argv.length; x++)
	{
//	    System.err.println("["+x+"] "+argv[x]);
	    IConfig port = init.LoadPort(argv[x]);

	    if (!port.cf_get_boolean(BriegelConf.k_csdb_query_versions,false))
		System.err.println("# "+
		    port.cf_get_str("@@port-name")+" not connected to CSDB");
//	    else 
//		System.err.println("query_versions="+
//		    port.cf_get_str(BriegelConf.k_csdb_query_versions));

	    VersionStack stk = port.getNewerVersions();
	    if (stk.size()>0)
		System.out.println(argv[x]+": "+stk);
	}
    }
}
