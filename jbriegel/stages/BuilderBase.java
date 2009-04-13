
package org.de.metux.briegel.stages;

import org.de.metux.briegel.conf.IConfig;

abstract public class BuilderBase extends Stage implements IBuilderRun
{
    public BuilderBase(String name, IConfig cf)
    {
	super(name,cf);
    }

    abstract public run_configure();
    abstract public run_preconfig();
    abstract public run_build();
    abstract public run_install();
}
