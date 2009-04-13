
package org.de.metux.briegel.stages;

import org.de.metux.briegel.base.EBriegelError;

public interface IBuilderRun
{
    // builder dependent
    public void run_preconfig()	        throws EBriegelError;
    public void run_configure()		throws EBriegelError;
    public void run_build()		throws EBriegelError;
    public void run_install()		throws EBriegelError;
}
