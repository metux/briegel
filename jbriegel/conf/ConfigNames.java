
package org.de.metux.briegel.conf;

public class ConfigNames
{
    public final static String SP_WorkingDir = "@@workdir";
    public final static String SP_PortName   = "@@port-name";

    /* The actual system-root path, where the current build will run.
       This variable is set by the SetupSysroot stage, based on target
       config and other considerations (eg. parallel builds may use
       a different path than the toolchain's standard sysroot)

       NOTE: in classes/styles/ports always use this one instead
       of old-fashioned $(system-root) !
    */
    public final static String SP_SystemRoot = "@@system-root";
}
