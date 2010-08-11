
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

    /* The current package version.
       Set by the port loading code in BriegelConf
    */
    public final static String SP_Version     = "@@version";
    public final static String SP_Version0    = "@@version0";
    public final static String SP_Version1    = "@@version1";
    public final static String SP_Version2    = "@@version2";
    public final static String SP_Version3    = "@@version3";

    /* A unique tag for the current feature selection for the
       current package. It does not necessarily contain the
       feature's ID's, even if it currently does.
       Set within the process-features stage in BriegelConf
    */
    public final static String SP_FeatureTag  = "@@feature-tag";

    /* The current package name.
       Set by the package loading code in BriegelConf
    */
    public final static String SP_PackageName = "@@package-name";

    /* The build root directory, where the tarball unpacking or
       vcs checkout happens to. This doesn't already have to be
       the actual source tree root, since it's usually sitting in
       subdirectory w/ package name and version.
    */
    public final static String SP_BuildRoot   = "@@buildroot";

    /* List of ports in "world" selection - these are the ports
       which are built by the BuildAll robot.
       Set by the config postprocessing stage in BriegelConf
    */
    public final static String SP_WorldSet    = "@@world";

    /* Global config file name (briegel.conf).
       Set by the main constructor/allocator of BriegelConf
       (normally coming from the calling command classes)
    */
    public final static String SP_Global_ConfigFile = "@@global-config-file";

    /* The actual system-root path, where the current build will run.
       This variable is set by the SetupSysroot stage, based on target
       config and other considerations (eg. parallel builds may use
       a different path than the toolchain's standard sysroot)

       NOTE: in classes/styles/ports always use this one instead
       of old-fashioned $(system-root) !
    */
    public final static String SP_SystemRoot = "@@system-root";

    /* The path where packages will install themselves to (aka DESTDIR).
       Set by the robots and used by builders and their styles
       (ie. passed to the individual package's buildscripts)
    */
    public final static String SP_InstallRoot = "@@install-root";
}
