package org.ginsim.core.annotation;

import org.ginsim.common.utils.OpenHelper;
import org.ginsim.common.utils.OpenUtils;

/**
 * MIRIAM-based OpenHelper: open MIRIAM URNs
 * MIRIAM provides a list of biological databases and the identifiers.org webservice
 * to open generic links (Database, identifier pairs) in these databases (supporting mirrors)
 * 
 * @author Aurelien Naldi
 */
public class DatabaseInfo implements OpenHelper {

	public final String name;
	public final String description;
	
	public DatabaseInfo(String name, String description) {
		this.name = name;
		this.description = description;
		
		OpenUtils.addHelperClass(name, this);
	}
	
	public void open() {
		OpenUtils.openURI("http://identifiers.org/"+name);
	}
	
	@Override
	public boolean open(String proto, String value) {
	    return OpenUtils.openURI(getLink(proto, value));
	}

	@Override
	public void add(String proto, String value) {
	}

	@Override
	public String getLink(String proto, String value) {
		return "http://identifiers.org/"+name+"/"+value;
	}

}
