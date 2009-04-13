
package org.de.metux.briegel.stages;

import java.io.File;

import org.de.metux.util.URLDownloader;
import org.de.metux.util.mkdir;
import org.de.metux.util.StoreFile;
import org.de.metux.util.FileOps;

import org.de.metux.briegel.conf.IConfig;
import org.de.metux.briegel.conf.ConfigNames;

import org.de.metux.briegel.base.EPropertyMissing;
import org.de.metux.briegel.base.EPropertyInvalid;
import org.de.metux.briegel.base.EFetchSourceFailed;

public class FetchSource extends Stage
{
    protected String filename;

    class Callback implements URLDownloader.Notify
    {
	Stage stage;
	public Callback(Stage s)
	{
	    stage = s;
	}
	
	public void debug(String msg)
	{
	    stage.debug("Download: "+msg);
	}
	
    	public void msg(String msg)
	{
	    stage.notice("Download: "+msg);	
	}
	
	public void progress(int percent, int size_got, int size_total)
	{
	    stage.notice("Download: "+percent+"% -- "+size_got+"/"+size_total+" bytes");
	}
    }
    
    public FetchSource(IConfig cf)
	throws EPropertyMissing, EPropertyInvalid
    {
	super("FETCHSOURCE", cf);
    }

    private boolean check_exists()
	throws EPropertyInvalid, EPropertyMissing
    {
	filename = config.getPropertyString("source-file");
	debug("source file: "+filename);

	/* check if the source file name is okay ... necessary ? */
	File file_handle = new File (filename);
	mkdir.mkdir_recursive(file_handle.getParentFile().getAbsolutePath());

	if (file_handle.exists())
	{
	    debug("Source file already exists. No fetch necessary.");
	    return true;
	}
	return false;
    }
    
    /* dirty hack: uses external curl */
    private boolean download()
	throws EPropertyInvalid
    {
	String urls[] = cf_list("fetch-source-url");
	URLDownloader dn = new URLDownloader(new Callback(this));
	
	for (int x=0; x<urls.length; x++)
	{
	    notice("now running download from "+urls[x]);
	    if (dn.download(urls[x],filename,true))
	    {
		StoreFile.store(filename+".AUTO", urls[x]+"\n");
		return true;
	    }
	    notice("failed :(");
	}
	error("could not download from any source location. bailing out");
	return false;
    }
    
    public void run_stage() 
	throws EFetchSourceFailed, EPropertyInvalid, EPropertyMissing
    {
	/* if not yet existing, try to download */
	if (!check_exists())
	    if (!download())
	    {
		FileOps.rm(config.getPropertyString("source-file"));
		throw new EFetchSourceFailed(
		    config.getPropertyString(ConfigNames.SP_PortName));
	    }
    }
}
