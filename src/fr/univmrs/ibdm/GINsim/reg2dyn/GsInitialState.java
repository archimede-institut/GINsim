package fr.univmrs.ibdm.GINsim.reg2dyn;

import java.util.HashMap;
import java.util.Map;

import fr.univmrs.ibdm.GINsim.global.GsNamedObject;

public class GsInitialState implements GsNamedObject {
	String name;
	Map m = new HashMap();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
