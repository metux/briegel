
package org.de.metux.briegel.conf;

public class ConfigNames
{
    /* Working director - that's where we actually run the build.
       May be a subdir of @@srcdir.

       This variable is set by the individual builders within
       the preconfig stage
    */
    public final static String SP_WorkingDir = "@@workdir";

    /* The name of the current port to build.
       It is set by the port loading code in BriegelConf
    */
    public final static String SP_PortName   = "@@port-name";

    /* The current package name.
       Set by the package loading code in BriegelConf
    */
    public final static String SP_PackageName = "@@package-name";

    /* The actual system-root path, where the current build will run.
       This variable is set by the SetupSysroot stage, based on target
       config and other considerations (eg. parallel builds may use
       a different path than the toolchain's standard sysroot)

       NOTE: in classes/styles/ports always use this one instead
       of old-fashioned $(system-root) !
    */
    public final static String SP_SystemRoot = "@@system-root";
}
