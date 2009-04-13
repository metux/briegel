
package org.de.metux.briegel.cmd;

import org.de.metux.util.Environment;
import org.de.metux.log.LoggerDummy;

import org.de.metux.briegel.base.EMisconfig;
import org.de.metux.briegel.base.EMissingPort;

import org.de.metux.briegel.conf.IConfig;
import org.de.metux.briegel.conf.BriegelConf;

public class Init
{
    static String default_configfile = "/etc/briegel/briegel.conf";
    
    public String config_file;
    public boolean silent = false;
    
    public Init()
    {
	config_file = Environment.getenv("BRIEGEL_CONF");
	if ((config_file==null)||config_file.equals(""))
	    config_file=default_configfile;
    }

    public IConfig LoadGlobal()
	throws EMisconfig
    {
	if (silent)
	    return new BriegelConf(config_file,new LoggerDummy());
	else
	    return new BriegelConf(config_file);
    }

    public IConfig LoadPort(String name)
	throws EMisconfig, EMissingPort
    {
	IConfig cf = LoadGlobal();
	cf.LoadPort(name);
	return cf;
    }
}
