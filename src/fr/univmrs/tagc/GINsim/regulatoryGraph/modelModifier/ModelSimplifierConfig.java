package fr.univmrs.tagc.GINsim.regulatoryGraph.modelModifier;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import fr.univmrs.tagc.GINsim.annotation.Annotation;
import fr.univmrs.tagc.common.datastore.MultiColHelper;
import fr.univmrs.tagc.common.datastore.NamedObject;
import fr.univmrs.tagc.common.xml.XMLWriter;
import fr.univmrs.tagc.common.xml.XMLize;


public class ModelSimplifierConfig implements NamedObject, XMLize, MultiColHelper {
	String name;
	Annotation note = new Annotation();
	Map m_removed = new HashMap();
	boolean	strict = true;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return name;
	}
	
	public void toXML(XMLWriter out, Object param, int mode)
			throws IOException {
		Iterator it = m_removed.keySet().iterator();
		if (!it.hasNext()) {
			return;
		}
		String s_removed = "";
		while (it.hasNext()) {
			s_removed += " "+it.next();
		}
		out.openTag("simplificationConfig");
		out.addAttr("name", this.name);
		out.addAttr("strict", ""+this.strict);
		out.addAttr("removeList", s_removed.substring(1));
		note.toXML(out, param, mode);
		out.closeTag();
	}
	
	public Object getVal(Object o, int index) {
		if (index == 1) {
			return m_removed.containsKey(o) ? Boolean.TRUE : Boolean.FALSE;
		}
		return o;
	}
	public boolean setVal(Object o, int index, Object value) {
		if (index == 1) {
			if (value.equals(Boolean.TRUE)) {
				m_removed.put(o, null);
			} else {
				m_removed.remove(o);
			}
			return true;
		}
		return false;
	}
}
