package org.ginsim.core.annotation;

import org.ginsim.common.utils.OpenUtils;
import org.ginsim.common.utils.OpenHelper;

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
