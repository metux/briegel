
package org.de.metux.briegel.robots;

import org.de.metux.briegel.base.EPropertyMissing;
import org.de.metux.briegel.base.EPropertyInvalid;
import org.de.metux.briegel.base.EMissingPort;
import org.de.metux.briegel.base.EMisconfig;
import org.de.metux.briegel.base.EBriegelError;

import org.de.metux.briegel.conf.IConfig;
import org.de.metux.briegel.conf.BriegelConf;

import org.de.metux.briegel.stages.Stage;

import org.de.metux.util.UniqueNameList;

/* this command fetches the source of a given port */
public class BulkBuild extends Stage
{
    public BulkBuild(String my_port, IConfig my_conf)
	throws EMissingPort, EPropertyMissing, EPropertyInvalid
    {
	super("BULK-BUILD", my_conf);
	if (my_conf==null)
	    throw new EMissingPort(my_port);
    }

    public BulkBuild(IConfig my_conf)
	throws EMissingPort, EPropertyInvalid, EPropertyMissing
    {
	super("BULK-BUILD",my_conf);
	if (my_conf==null)
	    throw new EMissingPort("<NULL>");
    }

    String[] features;
    boolean[] flags;
    
    public void buildnow()
	throws EMisconfig, EMissingPort, EBriegelError
    {
	String tag = "";
	IConfig variant = ((BriegelConf)config).allocConfig();
	for (int x=0; x<features.length; x++)
	{
	    tag += (flags[x]?"+":"-")+features[x];
	    String n = "feature-enable="+features[x];
	    /* the $(@@feature-enable) always takes precedence over $(feature-enable)
	       this way we can override the settings in the port-conf */
	    variant.cf_set("feature-enable="+features[x],(flags[x]?"true":"false"));
	    variant.cf_set("@@feature-enable="+features[x],(flags[x]?"true":"false"));
	}
	error("TAG: "+tag);
	variant.LoadPort(config.getPortName());
	RecursiveBuild bot = new RecursiveBuild(variant);
	bot.run();
    }
    
    public void step(int x)
	throws EMisconfig, EMissingPort, EBriegelError
    {
	if (x<features.length)
	{
	    flags[x] = true;
	    step(x+1);
	    flags[x] = false;
	    step(x+1);
	}
	else
	    buildnow();
    }

    public void run_stage()
	throws EBriegelError
    {
	features = cf_list("feature-declare");
	flags = new boolean[features.length];
	
	for (int x=0; x<features.length; x++)
	    notice("Feature: "+features[x]);

	if (features.length==0)
	    throw new RuntimeException("no features defined: fixme !");

	step(0);
    }
}
