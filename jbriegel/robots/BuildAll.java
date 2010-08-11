
package org.de.metux.briegel.robots;

import java.io.File;
import org.de.metux.briegel.conf.IConfig;
import org.de.metux.briegel.conf.ConfigNames;
import org.de.metux.briegel.stages.Stage;
import org.de.metux.briegel.base.EPropertyMissing;
import org.de.metux.briegel.base.EPropertyInvalid;
import org.de.metux.briegel.base.EBriegelError;
import org.de.metux.briegel.base.EMissingPort;

/* this command fetches the source of a given port */
public class BuildAll extends Stage
{
    public BuildAll(IConfig my_conf)
	throws EPropertyMissing, EPropertyInvalid
    {
	super("BUILD-ALL",my_conf);
    }

    public void run_stage()
	throws EBriegelError
    {
	String[] selection = config.getWorldList();

	for (int x=0; x<selection.length; x++)
	{
	    debug("building port recursive: "+selection[x]);
	    IConfig cf = config.allocLoadPort(selection[x]);
	    if (cf==null) 
		throw new EMissingPort(selection[x]);

	    RecursiveBuild bot = new RecursiveBuild(cf);
	    bot.run();
	}    
    }
}
