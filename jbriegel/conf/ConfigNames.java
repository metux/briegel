
package org.de.metux.briegel.conf;

public class ConfigNames
{
    /* Magic declarations. Processed by BriegelConf loading */
    public final static String MP_Package   = "@package";
    public final static String MP_Port      = "@port";
    public final static String MP_Style     = "@style";
    public final static String MP_Version   = "@version";

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

    /* The path where package's (briegel-generated) metadata, eg.
       config dump, file classifications, etc will be written to.
       This data is useful for various postprocessings (QM-checks)
       and binpkg generation.
       Set by the robots and used by builds and their styles.
    */
    public final static String SP_MetaRoot    = "@@meta-root";

    /* Most source tarballs are come with in an subdir within the
       archive (eg. $package-$version/). This variable contains
       the name of that subdir, detected in the prepare stage
       (right after decompression), so we can find the actual
       source tree (where Makefile+co are residing)
    */
    public final static String SP_SrcTree     = "@@srctree";

    /* This is the actual root of the source tree, where everything
       sits in. In most cases it's a subdirectory of @@buildroot,
       since most source tarballs are packed w/ subdirs (eg. named
       by packaga and version). It's also the directory where
       patching happens into. Set by the prepare stage.
    */
    public final static String SP_SrcRoot     = "@@srcroot";

    /* Sometimes packages unusally have their source tree - or at least
       the build files - in some subdir (eg. if distinct ports in their
       own subdirs). That's why we add an optional source-prefix and let
       the builder run there, rather than directly in @@srcroot.
       Set by prepare stage and used by builders and their styles.
    */
    public final static String SP_SrcDir      = "@@srcdir";

    /* A 20-byte hex random key which is different to each BriegelConf
       instance, can eg. be used to separate out build and sysroot
       directories,
    */
    public final static String SP_RandomKey   = "@@random-key";

    /* Git source repository and ref name

       If this property is set, fetch via git instead of web download.
       The remote ref name and the cache repository name also must be set.
    */

    /* Git command to run: we dont hardcode it, so you can choose a different
       one (eg. for using wrappers or if you have multiple installations)
    */
    public final static String Git_Command            = "git-command";

    /* Git source cache: this is a big local repository, where all remote
       refs are first pulled in (by the FetchSource stage). From here then
       the checkouts will happen in Prepare stage.
    */
    public final static String Git_SourceCache        = "git-source-cache";

    /* The package's original source repository. From here FetchSource stage
       will retrieve the currnt source ref into the cache repository.
    */
    public final static String Git_SourceRepository   = "git-source-repository";

    /* The package release's git ref (normally a version tag) for our source.
       Used by FetchSource to prepare cache repo and FetchSource configuration.
    */
    public final static String Git_SourceRef          = "git-source-ref";

    /* Prefix (under the @@buildroot) where Prepare stage will checkout into.
       This is required to mimic the common tarball layout (see @@srcdir)
    */
    public final static String Git_SourcePrefix       = "git-source-prefix";

    /* Local git ref within the cache repository, where Prepare stage will
       actually check out from. Set by FetchSource stage.
    */
    public final static String SP_Git_SourceLocalRef  = "@@git-source-local-ref";

    /* Local cache repository to use by Prepare stage. Set by the FetchSource
       stage */
    public final static String SP_Git_SourceLocalRepo = "@@git-source-local-repo";

    /* Sysroot path. Set in the global configuration or target styles,
       used by SetupSysroot stage to install the sysroot image and set
       up @@system-root variable
    */
    public final static String SystemRoot_Directory   = "system-install-root";

    /* Sysroot image tarball. Uncompressed into sysroot path in the
       SetupSysroot stage
    */
    public final static String SystemRoot_Image       = "sysroot-image";

    /* Sysroot git repository and ref (optional). If set, the SetupSysroot
       stage will checkout from there instead of uncompressing a tarball
    */
    public final static String SystemRoot_GitRepo     = "sysroot-git-repo";
    public final static String SystemRoot_GitRef      = "sysroot-git-ref";
    public final static String SystemRoot_GitCmd      = "sysroot-git-cmd";

    /* Prefixes for stage logfiles and cmdfiles. Used by Stage::exec_step()
       and the a step name will be appendet */
    public final static String Stages_Logfile_Prefix  = "stages-logfile-prefix";
    public final static String Stages_Cmdfile_Prefix  = "stages-cmdfile-prefix";
}
