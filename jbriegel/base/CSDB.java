
/* 

    CSDB client class

*/

package org.de.metux.briegel.base;

import org.de.metux.util.*;

public class CSDB
{
    public class SourceURL
    {
	public String   pkgname;
	public Version  version;
	public String   encoding;
	public String[] url;
    }
	
    public static String csdb_prefix = "http://sourcefarm.metux.de/robots/";
}
