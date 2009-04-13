
package org.de.metux.briegel.builder_autoconf;

import org.de.metux.briegel.base.EBriegelError;
import org.de.metux.briegel.base.EPropertyMissing;
import org.de.metux.briegel.base.EPropertyInvalid;
import org.de.metux.briegel.conf.IConfig;
import org.de.metux.briegel.stages.Stage;
import org.de.metux.briegel.stages.IBuilderRun;

public class Builder extends Stage implements IBuilderRun
{
    public Builder(IConfig cf)
	throws EPropertyMissing, EPropertyInvalid
    {
	super("AUTOCONF",cf);
    }

    public void run_preconfig()	throws EBriegelError
    {
	new Preconfig(config).run();
    }
    
    public void run_configure()	throws EBriegelError
    {
	new Configure(config).run();
    }
    
    public void run_build() throws EBriegelError
    {
	new Build(config).run();
    }
    
    public void run_install() throws EBriegelError
    {
	new Install(config).run();
    }
    
    public void run_stage() throws EBriegelError
    {
	run_preconfig();
	run_configure();
	run_build();
	run_install();
    }
}
