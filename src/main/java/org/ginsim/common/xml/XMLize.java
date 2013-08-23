package org.ginsim.common.xml;

import java.io.IOException;

/**
 * object that can save themself to XML must implement this interface to let others know.
 */
public interface XMLize {
    
    /**
     * write the XML representation of this object to a file
     * 
     * @param out
     * @throws IOException
     */
	public void toXML(XMLWriter out) throws IOException;	
}
