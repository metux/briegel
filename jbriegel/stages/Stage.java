
package org.de.metux.briegel.stages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InterruptedIOException;
import java.util.Properties;

import org.de.metux.log.ILogger;
import org.de.metux.briegel.conf.IConfig;
import org.de.metux.briegel.conf.ConfigNames;
import org.de.metux.briegel.base.EBriegelError;
import org.de.metux.briegel.base.EMisconfig;
import org.de.metux.briegel.base.EPropertyInvalid;
import org.de.metux.briegel.base.EPropertyMissing;

import org.de.metux.util.Exec;
import org.de.metux.util.StrReplace;
import org.de.metux.util.StoreFile;

import org.de.metux.propertylist.EIllegalValue;

abstract public class Stage
{
    protected String name;
    protected IConfig config;
    protected ILogger logger;
    protected String port_name;

    public Stage ( String my_name, IConfig my_config )
	throws EPropertyMissing, EPropertyInvalid
    {
	config = my_config;
	name   = my_name;
	logger = my_config.getLogger();

	/* add working process id (ie. port name) if available */
	String _id = config.cf_get_str("@@id", null);
	if (_id!=null)
	    name = name+"{"+_id+"}";
	
	port_name = config.getPropertyString(ConfigNames.SP_PortName);
    }

    /* logging stuff */    
    public boolean error(String text)
    {
	logger.error(name,text);
	return false;
    }
    public void warning(String text)
    {
	logger.error(name,text);
    }
    public void notice(String text)
    {
	logger.notice(name,text);
    }    
    public void debug(String text)
    {
	logger.debug(name,text);
    }
    
    public abstract void run_stage() throws EBriegelError;
    
    public void run() throws EBriegelError
    {
	try 
	{ 
	    run_stage(); 
	}
	catch (EBriegelError e)
	{
	    logger.error(name,"Stage ERROR: "+e);
	    throw e;
	    // return false;
	}
    }
    
    public boolean mkdir(String dir)
    {
	File handle;
	
	debug("Creating directory: "+dir);
	handle = new File(dir);
	handle.mkdirs();
	
	if (handle.isDirectory())
	    return true;
	
	return error("Could not create directory: "+dir);
    }

    private class exec_cb implements Exec.IExecCallback
    {
	FileWriter writer;
	Stage stage;
	int log_exec_stdout;
	int log_exec_stderr;
	
	static final private int loglevel_stdout = -1;
	static final private int loglevel_stderr = -2;
	static final private int loglevel_debug  = 1;
	static final private int loglevel_notice = 2;
	static final private int loglevel_warning = 3;
		
	private int decode_loglevel(String s)
	{
	    if (s==null) return loglevel_notice;
	    if (s.equals("stdout")) return loglevel_stdout;
	    if (s.equals("stderr")) return loglevel_stderr;
	    if (s.equals("debug")) return loglevel_debug;
	    if (s.equals("notice")) return loglevel_notice;
	    if (s.equals("warning")) return loglevel_warning;
	    return loglevel_notice;
	}

	private void writeline(int level, String line)
	{
	    switch(level)
	    {
		case loglevel_stdout:
		    System.out.println(line);
		break;
		case loglevel_stderr:
		    System.err.println(line);
		break;
		case loglevel_debug:
		    debug(line);
		break;
		case loglevel_notice:
		    notice(line);
		break;
		case loglevel_warning:
		    notice(line);
		break;
		default:
		    error("[!!!] "+line);	    
	    }
	}
	
	public void log(String line)
	{
	    if (writer!=null)
		try 
		{
		    writer.write(line+"\n");
		    writer.flush();
		}
		catch (IOException e)
		{
		    stage.error("error writing to output stream: "+e);
		}
	}
	
	public exec_cb(Stage s,FileWriter w)
	{
	    writer = w;
	    stage = s;
	    try
	    {
		log_exec_stderr = decode_loglevel(stage.config.cf_get_str("log-exec-stderr"));
		log_exec_stdout = decode_loglevel(stage.config.cf_get_str("log-exec-stdout"));
	    } catch (Exception e)
	    {
		error(e.toString());
	    }
	}
	
	public void OutputLine(String text)
	{
	    log(text);
	    writeline(log_exec_stdout,">> "+text);
	}
	
	public void ErrorLine(String text)
	{
	    log(text);
	    writeline(log_exec_stderr,"!> "+text);
	}
	
	public void debug(String text)
	{
	    if ((text!=null) && 
	        (!text.trim().equals("subprocess exited with: 0")))
	        stage.debug(">> "+text);
	}
	
	public void error(String text)
	{
	    stage.error("EXEC: "+text);
	}
    }

    public boolean exec(String cmdline, String logfile, String cmdfile)
    {
	FileWriter logwriter;
	
	cmdline = StrReplace.replace("\n", " ",
		  StrReplace.replace("\r", " ", cmdline));

	if (cmdfile!=null)
	    if (!StoreFile.store(cmdfile, cmdline, "ugo+rx"))
		throw new RuntimeException("could not store cmd file: ");

	logwriter = null;
	if (logfile!=null)
	{
	    try
	    {
		logwriter = new FileWriter(logfile,false);
		logwriter.write("## executing: "+cmdline+"\n");
	    }
	    catch (IOException e)
	    {
		error("could not create logfile: "+logfile);
	    }
	}
	
	notice("executing: \""+cmdline+"\"");

	Exec exec = new Exec(new exec_cb(this,logwriter));
	return exec.run(cmdline);
    }

    public boolean exec_step(String step, String cmdline)
	throws EPropertyMissing, EPropertyInvalid
    {
	String workdir=config.getPropertyString(ConfigNames.SP_WorkingDir);
	if (!mkdir(workdir))
	{
	    error("exec_step "+step+" cannot create workdir "+workdir);
	    return false;
	}

	if (!StoreFile.store(workdir+"/BRIEGEL-cmd-"+step, cmdline,"ugo+rwx"))
	{
	    error("exec_step "+step+" storing cmdfile failed");
	    return false;
	}
	
	return exec(
	    cmdline+" ; export retcode=$?; echo; exit $retcode",
	    workdir+"/BRIEGEL-log-"+step,
	    workdir+"/BRIEGEL-cmd-"+step
	);
    }

    public boolean exec(String cmdline)
    {
	return exec(cmdline,null,null);
    }
    
//    protected boolean cf_load_content(String name, String fn)
//    {
//	return config.cf_load_content(name,fn);
//    }
    
    protected String[] cf_list(String name)
	throws EPropertyInvalid
    {
	return config.cf_get_list(name);
    }
    
    public boolean copytree(String src, String dest)
    {
	/* FIXME: check result! */
	notice("copying image files from \""+src+"\" to \""+dest+"\" (using tarpipe)" );
	return exec("mkdir -p "+dest+" && ((cd "+src+" && tar -c *) | (cd "+dest+" && tar -x)) && echo OKAY", null, null);
    }

    public boolean decompress_tar_bz2(String archive, String workdir)
    {
    	notice("decompressing \""+archive+"\" to \""+workdir+"\" (tar+bz2)");
	mkdir(workdir);
	return exec("cd "+workdir+" ; ls ; tar -xjf "+archive, null, null);
    }
}
