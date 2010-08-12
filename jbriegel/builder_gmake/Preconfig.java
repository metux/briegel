
package org.de.metux.briegel.builder_gmake;

import org.de.metux.briegel.base.EPropertyMissing;
import org.de.metux.briegel.base.EPropertyInvalid;
import org.de.metux.briegel.base.EMisconfig;
import org.de.metux.briegel.conf.IConfig;
import org.de.metux.briegel.conf.ConfigNames;
import org.de.metux.briegel.stages.Stage;

//#
//# NOTE: you may _NOT_ skip this stage - this produces really ugly things!
//# 

public class Preconfig extends Stage
{
    public Preconfig(IConfig cf)
	throws EPropertyMissing, EPropertyInvalid
    {
	super("GMAKE-PRECONFIG", cf);
    }
    
    public void run_stage() throws EMisconfig
    {
	notice("builder: gmake/j v0.2" );

	/* check if make rules for install/install-strip are defined */
	notice("we have to go into our own subdir: "+
	    config.getPropertyString("gmake-build-workdir" 
	));
	
	config.cf_set(ConfigNames.SP_WorkingDir, "$("+ConfigNames.SP_SrcDir+")/$(gmake-build-workdir)");
	debug("workdir:   "+config.cf_get_str_mandatory(ConfigNames.SP_WorkingDir));
    }
}
