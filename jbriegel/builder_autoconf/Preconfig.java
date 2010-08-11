
package org.de.metux.briegel.builder_autoconf;

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
	super("AC-PRECONFIG", cf);
    }
    
    public void run_stage() throws EMisconfig
    {
	notice("builder: amconfigure/j v0.3.1" );

	notice("we have to go into our own subdir: "+
	    config.getPropertyString("autoconf-build-workdir"));

	config.cf_set(ConfigNames.SP_WorkingDir, "$(@@srcdir)/$(autoconf-build-workdir)");

	debug("workdir:   "+config.cf_get_str(ConfigNames.SP_WorkingDir));
	debug("srcdir:    "+config.getPropertyString("@@srcdir"));
	debug("subdir:    "+config.getPropertyString("autoconf-build-workdir"));
    }
}
