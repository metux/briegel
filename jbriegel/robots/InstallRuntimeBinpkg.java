
package org.de.metux.briegel.robots;

import org.de.metux.briegel.base.EBriegelError;
import org.de.metux.briegel.base.EPropertyInvalid;
import org.de.metux.briegel.base.EPropertyMissing;
import org.de.metux.briegel.conf.IConfig;
import org.de.metux.briegel.conf.ConfigNames;
import org.de.metux.briegel.stages.Stage;

public class InstallRuntimeBinpkg extends Stage
{
    public InstallRuntimeBinpkg(IConfig my_conf)
	throws EPropertyInvalid, EPropertyMissing
    {
	super("INSTALL-RUNTIME-BINPKG",my_conf);
    }

    public void run_stage()
	throws EBriegelError
    {
	config.getPropertyString("image-install-root");
	config.getPropertyString("image-meta-root");

	config.cf_set(ConfigNames.SP_InstallRoot, 	"$(image-install-root)");
	config.cf_set("@@meta-root",    	"$(image-meta-root)");
	config.cf_set("system-install-root", 	"$(image-install-root)");
	config.cf_set("system-meta-root",    	"$(image-meta-root)");
	
	String[] names = cf_list(ConfigNames.SP_PortName);

	debug(" ==> installing: "+names[0]);
	
	debug(" --> RUNTIME-INSTALL_ROOT: "+
	    config.cf_get_str("image-install-root"));
	debug(" --> SYSTEM-INSTALL-ROOT: "+
	    config.cf_get_str("image-install-root"));
	
	InstallTree install_tree = new InstallTree(config,names);
	install_tree.run_stage();
    }
}
