
package org.de.metux.briegel.stages;

import java.io.File;
import java.io.IOException;

import org.de.metux.util.rm;
import org.de.metux.util.StrUtil;

import org.de.metux.briegel.base.EPrepareFailed;
import org.de.metux.briegel.base.EMisconfig;
import org.de.metux.briegel.base.EPropertyInvalid;
import org.de.metux.briegel.base.EPropertyMissing;

import org.de.metux.briegel.conf.IConfig;
import org.de.metux.briegel.conf.ConfigNames;

public class Prepare extends Stage
{
    protected String filename;
    
    public Prepare(IConfig cf)
	throws EPropertyMissing, EPropertyInvalid
    {
	super("PREPARE",cf);
    }

    public void run_stage() throws EPrepareFailed, EMisconfig, EPropertyInvalid
    {
	/* -- add some global vars -- */
	if (StrUtil.isEmpty(config.cf_get_str("source-buildroot")))
	    config.cf_set("@@buildroot", "$(src_dir)/$("+ConfigNames.SP_PackageName+")");
	else	
	    config.cf_set("@@buildroot", "$(source-buildroot)");
	config.cf_set("@@srcroot",   "$(@@buildroot)/$(@@srctree)");
	config.cf_set("@@srcdir",    "$(@@srcroot)/$(source-prefix)");

	/* -- prepare the sourcetree -- */
	prepare_sourcetree();

	/* -- init directories -- */
	String [] init_dirs = cf_list("init-dirs");
	for (int x=0; x<init_dirs.length; x++)
	    mkdir(init_dirs[x]);
	
	debug("source-prefix:  "+config.getPropertyString("source-prefix",""));
        debug("@@buildroot:    "+config.getPropertyString("@@buildroot"));
	debug("@@srcroot:      "+config.getPropertyString("@@srcroot"));
	debug("@@srcdir:       "+config.getPropertyString("@@srcdir"));
	debug("@@srctree:      "+config.getPropertyString("@@srctree"));
    }

    String sourcetree_marker() throws EMisconfig
    {
	return
	    config.cf_get_str(ConfigNames.SP_PackageName)+":"+
	    config.getPropertyString("source-package")+":"+
	    config.getPropertyString("version")+":"+
	    config.getPropertyString("patch-file")+":"+
	    config.getPropertyString("patch-file-name");
    }

    String detect_archiver ( String filename )
    {
	if (filename.endsWith(".tar.gz") || 
	    filename.endsWith(".tgz"))
	    return "tar.gz";
	    
	if (filename.endsWith(".tar.bz2") ||
	    filename.endsWith(".tbz") ||
	    filename.endsWith(".tbz2"))
	    return "tar.bz2";
	    
	return null;
    }
    
    void decompress_archive ( String source ) throws EMisconfig, EPrepareFailed
    {
	String buildroot = config.getPropertyString("@@buildroot");
	String archiver = config.getPropertyString("archiver","");
	if (archiver.length()==0)
	{
	    if ((archiver=detect_archiver(source))==null)
	    {
		error("cannot detect proper archiver for \""+source+"\"");
		throw new EMisconfig("cannot detect proper archiver for: "+source);
	    }
	    config.cf_set("archiver", archiver);
	}	    
    
	if (!mkdir(buildroot))
	    throw new EPrepareFailed ("cannot create buildroot");

	if (archiver.equals("tar.bz2"))
	{
	    notice("decompressing tar.bz2: "+source+" ..." );
	    if (!exec ( "cd "+buildroot+" && tar -xjf "+source ))
		throw new EPrepareFailed("error decompressing: "+source);
	}
	else if (archiver.equals("tar.gz"))
	{
	    notice("decompressing tar.gz: "+source+" ..." );
	    if (!exec ( "cd "+buildroot+" && tar -xzf "+source ))
		throw new EPrepareFailed("error decompressing: "+source);
	}
	else throw new EMisconfig("unsupported archiver: \""+archiver+"\"" );
    }

    /* pre-execute something *before* we start decompressing
       ie. for preparing patch sets */
       
    private void preexec() throws EPrepareFailed, EPropertyInvalid, EPropertyMissing
    {
	/* prepare patches */
	String preexec=config.getPropertyString("prebuild-patch-preexec","");
	if (preexec.length()==0)
	    return;
	    
	notice("Calling preexec: "+preexec);
	if (exec(preexec))
	    return;
	    
	error("preexec failed");
	throw new EPrepareFailed("preexec failed");
    }

    void cleanup() throws EMisconfig
    {
	String buildroot = config.getPropertyString("@@buildroot");
	if (buildroot.length()<10)
	    throw new EMisconfig("@@buildroot -> len < 10");
	    
	notice("cleaning up "+buildroot );
	rm.remove_recursive(buildroot);
	mkdir(buildroot);
    }
    
    void paranoia_check() throws EMisconfig, EPrepareFailed
    {
	/* check for proper buildroot variable ... probably not really necessary */
	String buildroot = config.getPropertyString("@@buildroot");
	try
	{
	    buildroot = new File(buildroot).getCanonicalPath();
	}
	catch (IOException e)
	{
	    error("exception: "+e);
	    throw new EPrepareFailed ("cannot access buildroot");
	}
	
	if (buildroot.length()<5)
	    throw new EMisconfig("corrupt @@buildroot variable - internal problem ?");
    
	/* -- get the source file name / uri -- */
	String source=config.getPropertyString("source-file");
    }

    void decompress() throws EMisconfig, EPrepareFailed
    {
	/* now run decompression */
	String srclist[] =cf_list("source-file");
	if (srclist.length==0)
	{
	    error("missing package source option in configuration");
	    throw new EMisconfig("missing package source option in configuration");
	}

	for (int x=0; x<srclist.length; x++)
	    decompress_archive(srclist[x]);
    }
        
    void prepare_sourcetree() throws EMisconfig, EPrepareFailed
    {
	paranoia_check();
	preexec();
	cleanup();
	decompress();
	lookup_srcdir();
	apply_patches();
    }

    /* right after decompression, we need to know our actual srcroot */
    void lookup_srcdir() throws EMisconfig, EPrepareFailed
    {
	String buildroot = config.getPropertyString("@@buildroot");

	File handle = new File(buildroot);
	String sublist[] = handle.list();
	
	if (sublist==null) 
	{
	    error("could not get directory listing!");
	    throw new EPrepareFailed("could not get directory listing!");
	}
	
	if (sublist.length==0)
	{
	    error("no srcdir found. seems like decompression failed");
	    throw new EPrepareFailed("no srcdir found. seems like decompression failed");
	}
		
	if (sublist.length>1)
	{
	    error("more than one srcdir found. looks like trouble");
	    throw new EPrepareFailed("more than one srcdir found. looks like trouble");
	}
	
	debug("srcdir is: "+sublist[0]);
	
	String srcroot = buildroot+"/"+sublist[0];
	debug("srcroot is: "+srcroot);
	
	config.cf_set("@@srctree", sublist[0]);
    }
    
    void apply_patches() throws EMisconfig, EPrepareFailed
    {
	String patches[]  = cf_list("prebuild-patch-file");
	String patch_opts = config.getPropertyString("prebuild-patch-opts","");
	String srcroot    = config.getPropertyString("@@srcroot");
	
	for (int x=0; x<patches.length; x++)
	{
	    if (!(new File(patches[x]).exists()))
	    {
		error("could not load patch file: "+patches[x]);
		throw new EPrepareFailed ("could not load patch file: "+patches[x]);
	    }

	    notice("applying patch: "+patches[x]+"("+patch_opts+")" );
	    if (!exec("cd "+srcroot+";pwd;patch "+patch_opts+"<"+patches[x]))
	    {
		error("could not apply patch");
		throw new EPrepareFailed("could not apply patch");
	    }
	}
    }
}
