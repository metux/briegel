
package org.de.metux.briegel.builder_autoconf;

import org.de.metux.util.StrReplace;
import org.de.metux.util.StoreFile;
import org.de.metux.propertylist.EIllegalValue;

import org.de.metux.briegel.stages.Stage;
import org.de.metux.briegel.base.EMisconfig;
import org.de.metux.briegel.base.EConfigureFailed;
import org.de.metux.briegel.base.EPropertyInvalid;
import org.de.metux.briegel.base.EPropertyMissing;
import org.de.metux.briegel.conf.IConfig;
import java.io.File;

public class Configure extends Stage
{
    public Configure(IConfig cf)
	throws EPropertyMissing, EPropertyInvalid
    {
	super("AC-CONF", cf);
    }
    
    private void createconf()
	throws EPropertyInvalid, EPropertyMissing, EConfigureFailed
    {
	String createconf = config.getPropertyString("autoconf-exec-createconf","");
	if (createconf.length()==0)
	    return;
	    
	if (!exec_step("createconf", createconf))
	{
	    error("createconf command failed");
	    throw new EConfigureFailed(current_port_name);
	}
    }
    
    private void load_opts() 
	throws EMisconfig
    {
	/* load the options */
	String optfile = config.getPropertyString("!notnull!autoconf-uri-optfile");
	debug("using optfile: "+optfile);

	/* pipe the build options through the config processor */
	config.cf_load_content( "@@builder-autoconf-options", new File(optfile) );

	// NOT USING RAW!
	String opts = config.getPropertyString("@@builder-autoconf-options");
	opts = StrReplace.replace("\n"," ",
	       StrReplace.replace("\r"," ", opts));
	
	// now @@builder-autoconf-options contains exactly whats 
	// passed to ./configure via commandline
	config.cf_set("@@builder-autoconf-options", opts);

	notice("Using optins: "+opts);
    }

    void run_configure() throws EMisconfig, EConfigureFailed
    {
	String workdir = config.getPropertyString("@@workdir");
	String cmd     = config.getPropertyString("autoconf-exec-configure");
	String env     = config.getPropertyString("autoconf-env-configure");
	String opts    = config.getPropertyString( "@@builder-autoconf-options" );

	if (!mkdir(workdir))
	{
	    error("could not create working directory \""+workdir+"\"");
	    throw new EConfigureFailed(current_port_name);
	}

	String cmdline = "cd "+workdir+" && "+env+" "+cmd+" "+opts;
	
	if (!StoreFile.store(workdir+"/BRIEGEL-cmd-configure",cmdline,"ugo+rx"))
	    throw new EConfigureFailed("could not store cmdfile");

	if (!exec(
	    cmdline+"; export retcode=$?; echo; exit $retcode",
	    workdir+"/BRIEGEL-log-configure",
	    workdir+"/BRIEGEL-cmd-configure"
	)) {
	    error("configure command failed");
	    throw new EConfigureFailed(current_port_name);
	}
    }

    void fixup_makefile() throws EMisconfig
    {
	String workdir = config.getPropertyString("@@workdir");

        // FIX for one of dozens autoshit bugs 
	if (config.cf_get_boolean("autoconf-bugfix-touch-makefile",false))
	{
	    debug("Fixing Makefile timestamp due autoshit tends to set them into the past ...");
	    exec(
		"cd "+workdir+" && touch `find -name Makefile`",
		workdir+"/BRIEGEL-log-fixup-makefile",
		workdir+"/BRIEGEL-cmd-fixup-makefile"
	    );
	}
    }    
    
    public void run_stage() throws EMisconfig, EConfigureFailed
    {
	createconf();
	load_opts();
	run_configure();
	fixup_makefile();
    }
}
