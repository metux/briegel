
package org.de.metux.briegel.robots;

import org.de.metux.briegel.base.EBriegelError;
import org.de.metux.briegel.conf.IConfig;
import org.de.metux.briegel.stages.Stage;
import org.de.metux.briegel.base.EBriegelError;
import org.de.metux.briegel.base.EPropertyMissing;
import org.de.metux.briegel.base.EPropertyInvalid;

import java.io.File;

public class CreateSystemImage extends Stage
{
    public CreateSystemImage(IConfig my_conf)
	throws EPropertyMissing, EPropertyInvalid
    {
	super("CREATE-SYSIMAGE",my_conf);
    }

    public void run_stage()
	throws EBriegelError
    {
	config.getPropertyString("image-install-root");
	config.getPropertyString("image-meta-root");
	config.cf_set("@@install-root", "$(image-install-root)");
	config.cf_set("@@meta-root",    "$(image-meta-root)");
	
	config.cf_load_content("@selection", 
	    new File(config.getPropertyString("selection-file")));
	String[] selection = cf_list("@selection");

	InstallRuntimeTree install_tree = new InstallRuntimeTree(config,selection);
	install_tree.run_stage();
    }
}
