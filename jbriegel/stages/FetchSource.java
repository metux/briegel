
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

    private String _git_source_repository;
    private String _git_source_cache;
    private String _git_source_local_ref;
    private String _git_source_ref;
    private String _git_source_directory;
    private String _git_command;

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

    private void fetch_git()
	throws EPropertyInvalid, EPropertyMissing, EFetchSourceFailed
    {
	_git_source_cache = config.cf_get_str_mandatory(ConfigNames.Git_SourceCache);
	_git_source_ref   = config.cf_get_str_mandatory(ConfigNames.Git_SourceRef);
	_git_command      = config.cf_get_str_mandatory(ConfigNames.Git_Command);

	/* compute the local ref name */
	_git_source_local_ref =
	    "refs/tags/_BRIEGEL_MIRROR."+
	    config.cf_get_str(ConfigNames.SP_PortName)+"/"+
	    config.cf_get_str(ConfigNames.SP_Version);

	notice("Using git source repository: \""+_git_source_repository+"\"");
	notice("           cache repository: \""+_git_source_cache+"\"");

	/* initialize the master git repository */
	if (!exec(_git_command+" init --bare "+_git_source_cache))
	    throw new EFetchSourceFailed(config.getPortName()+": git cache repository init failed");

	/* fetch the remote refs into our cache repository */
	String _cmd = _git_command+" --git-dir=\""+_git_source_cache+"\" ";
	if (!exec(
	    "if "+_cmd+" show-ref \""+_git_source_local_ref+"\" ; then echo \"Already got local ref\" ; else "+
	    _cmd+" fetch --no-tags \""+_git_source_repository+
	    "\" \"+"+_git_source_ref+":"+_git_source_local_ref+"\" || exit 1 ; fi"))
	    throw new EFetchSourceFailed(config.getPortName()+": remote fetch failed");

	/* set properties for subsequent stages (eg. Prepare) */
	config.cf_set(ConfigNames.SP_Git_SourceLocalRepo, _git_source_cache);
	config.cf_set(ConfigNames.SP_Git_SourceLocalRef,  _git_source_local_ref);
    }

    public void run_stage() 
	throws EFetchSourceFailed, EPropertyInvalid, EPropertyMissing
    {
	/* if a git repository is set, take this instead of web url */
	_git_source_repository = config.cf_get_str(ConfigNames.Git_SourceRepository,"");
	if (!_git_source_repository.isEmpty())
	{
	    fetch_git();
	    return;
	}

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
