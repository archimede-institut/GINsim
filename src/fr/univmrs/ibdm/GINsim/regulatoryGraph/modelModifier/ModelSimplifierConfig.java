package fr.univmrs.ibdm.GINsim.regulatoryGraph.modelModifier;

import java.util.Map;
import java.util.HashMap;

import fr.univmrs.ibdm.GINsim.annotation.Annotation;
import fr.univmrs.tagc.datastore.NamedObject;


public class ModelSimplifierConfig implements NamedObject {
	String name;
	Annotation note = new Annotation();
	Map m_removed = new HashMap();
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
