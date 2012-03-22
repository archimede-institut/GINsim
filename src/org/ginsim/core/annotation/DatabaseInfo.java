package org.ginsim.core.annotation;

import org.ginsim.common.OpenHelper;
import org.ginsim.common.utils.IOUtils;

public class DatabaseInfo implements OpenHelper {

	public final String name;
	public final String description;
	
	public DatabaseInfo(String name, String description) {
		this.name = name;
		this.description = description;
		
		IOUtils.addHelperClass(name, this);
	}
	
	public void open() {
		IOUtils.openURI("http://identifiers.org/"+name);
	}
	
	@Override
	public boolean open(String proto, String value) {
	    return IOUtils.openURI(getLink(proto, value));
	}

	@Override
	public void add(String proto, String value) {
	}

	@Override
	public String getLink(String proto, String value) {
		return "http://identifiers.org/"+name+"/"+value;
	}

}
