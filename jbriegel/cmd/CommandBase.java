/*

    Base class for all command line frontends

*/

package org.de.metux.briegel.cmd;


import org.de.metux.util.Environment;

import org.de.metux.log.ILogger;
import org.de.metux.log.LoggerDummy;

import org.de.metux.briegel.base.EBriegelError;
import org.de.metux.briegel.base.EMisconfig;
import org.de.metux.briegel.base.EMissingPort;
import org.de.metux.briegel.base.FBriegelLogger;

import org.de.metux.briegel.conf.IConfig;
import org.de.metux.briegel.conf.IConfig;
import org.de.metux.briegel.conf.BriegelConf;


public abstract class CommandBase
{
    /* by setting this variable, clients can inject their own config file */
    public String config_file;

    /* my program name */
    public String myname;

    /* suppress unwanted outputs */
    public boolean silent = false;

    /* logger object */
    public ILogger logger;

    public static final int exitcode_okay = 0;
    public static final int exitcode_err_config_param  = 1;
    public static final int exitcode_err_global_config = 2;
    public static final int exitcode_err_missing_port  = 3;
    public static final int exitcode_err_port_config   = 4;
    public static final int exitcode_err_exception     = 99;

    public CommandBase(String n)
    {
	myname = n;
    }

    public String getConfigFile()
    {
	if (config_file != null)
	    return config_file;

	String cf = Environment.getenv("BRIEGEL_CONF");
	if ((cf == null) || (cf.equals("")))
	{
	    System.err.println(myname+": missing config file. please set the $BRIEGEL_CONF environment variable");
	    System.exit(exitcode_err_config_param);
	}

	return cf;
    }

    public ILogger getLogger()
    {
	if (logger != null)
	    return logger;

	if (silent)
	    logger = new LoggerDummy();
	else
	    logger = FBriegelLogger.alloc();

	return logger;
    }

    public void errexit(String msg, int exitcode, Exception e)
    {
	System.err.println(myname+" :"+msg);
	System.err.println(e);
	System.err.println(e.getMessage());
	System.err.flush();
	System.out.flush();
	System.exit(exitcode);
    }

    public IConfig getGlobalConfig()
    {
	try
	{
	    return new BriegelConf(getConfigFile(),getLogger());
	}
	catch (EMisconfig e)
	{
	    errexit("error loading global config", exitcode_err_global_config, e);
	}
	return null;
    }

    public IConfig getPortConfig(String name)
    {
	try
	{
	    IConfig cf;
	    cf = getGlobalConfig();
	    cf.LoadPort(name);
	    return cf;
	}
	catch (EMissingPort e)
	{
	    errexit(" missing port: "+name, exitcode_err_missing_port, e);
	}
	catch (EMisconfig e)
	{
	    errexit("error loading port config for: "+name, exitcode_err_port_config, e);
	}
	return null;
    }

    public abstract void cmd_main(String argv[]) throws EBriegelError;

    public void run(String argv[])
    {
	try
	{
	    cmd_main(argv);
	}
	catch (EBriegelError e)
	{
	    errexit("exception", exitcode_err_exception, e);
	}
    }
}
