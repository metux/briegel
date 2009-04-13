
package org.de.metux.briegel.base;

public class EMissingFeatureSwitch extends EMisconfig
{
    public EMissingFeatureSwitch(String port, String feature)
    {
	super(port+"->"+feature);
    }
}
