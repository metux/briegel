
package org.de.metux.briegel.conf;

import org.de.metux.briegel.base.EPropertyInvalid;
import org.de.metux.briegel.base.EPropertyMissing;
import org.de.metux.briegel.base.EMissingGlobalConf;
import org.de.metux.briegel.base.EMissingStyle;
import org.de.metux.briegel.base.EMisconfig;
import org.de.metux.briegel.base.EMissingPackage;
import org.de.metux.briegel.base.EMissingPort;
import org.de.metux.log.ILogger;
import org.de.metux.util.Version;
import org.de.metux.util.VersionStack;
import org.de.metux.util.UniqueNameList;
import java.net.URL;
import java.io.File;

public interface IConfig
{
    public void error(String text);
    public void warning(String text);
    public void notice(String text);
    public void debug(String text);

    public void store(String filename);

    public void cf_set(String name, String value);
    public String cf_get_str_mandatory(String name) 
	throws EPropertyInvalid, EPropertyMissing;	
    public String cf_get_str(String name)
	throws EPropertyInvalid;
    public String cf_get_str(String name, String def);

    public boolean cf_get_boolean(String name)
	throws EPropertyInvalid;
    public boolean cf_get_boolean(String name, boolean def)
	throws EPropertyInvalid;

//    public URL cf_get_url(String name, URL url)
//	throws EPropertyInvalid;
//    public URL cf_get_url(String name)
//	throws EPropertyInvalid, EPropertyMissing;

    public IConfig alloc() 
	throws EMisconfig;
    public IConfig allocLoadPort(String port)
	throws EMisconfig, EMissingPort, EMissingPackage;
    public boolean LoadPort(String port)
	throws EMisconfig, EMissingPort, EMissingPackage;

    public String[] cf_get_list(String name) throws EPropertyInvalid;
    
    public boolean cf_load_content(String field, File filename);
    public ILogger getLogger();

    public Version      getVersion() throws EPropertyInvalid, EPropertyMissing;
    public VersionStack getAvailableVersions() throws EPropertyInvalid;
    public VersionStack getNewerVersions() throws EPropertyInvalid, EPropertyMissing;
    public String[] getWorldList() throws EPropertyInvalid, EPropertyMissing;
    public String   getPortName() throws EPropertyInvalid, EPropertyMissing;
    public UniqueNameList getImmutableList();

    public String getPropertyString(String name, String def)
	throws EPropertyInvalid, EPropertyMissing;
    public String getPropertyString(String name)
	throws EPropertyInvalid, EPropertyMissing;
}
