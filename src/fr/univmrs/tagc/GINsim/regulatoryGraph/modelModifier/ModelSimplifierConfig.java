package fr.univmrs.tagc.GINsim.regulatoryGraph.modelModifier;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.univmrs.tagc.GINsim.annotation.Annotation;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.common.datastore.MultiColHelper;
import fr.univmrs.tagc.common.datastore.NamedObject;
import fr.univmrs.tagc.common.xml.XMLWriter;
import fr.univmrs.tagc.common.xml.XMLize;


public class ModelSimplifierConfig implements NamedObject, XMLize, MultiColHelper<GsRegulatoryVertex> {
	String name;
	Annotation note = new Annotation();
	Map<GsRegulatoryVertex, List<GsRegulatoryVertex>> m_removed = new HashMap<GsRegulatoryVertex, List<GsRegulatoryVertex>>();
	boolean	strict = true;

	@Override
	public String getName() {
		return name;
	}
	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	@Override
	public void toXML(XMLWriter out, Object param, int mode)
			throws IOException {
		if (m_removed.size() < 1) {
			return;
		}
		String s_removed = "";
		for (GsRegulatoryVertex v: m_removed.keySet()) {
			s_removed += " "+v;
		}
		out.openTag("simplificationConfig");
		out.addAttr("name", this.name);
		out.addAttr("strict", ""+this.strict);
		out.addAttr("removeList", s_removed.substring(1));
		note.toXML(out, param, mode);
		out.closeTag();
	}
	
	@Override
	public Object getVal(GsRegulatoryVertex o, int index) {
		if (index == 1) {
			return m_removed.containsKey(o) ? Boolean.TRUE : Boolean.FALSE;
		}
		return o;
	}
	@Override
	public boolean setVal(GsRegulatoryVertex vertex, int index, Object value) {
		if (index == 1) {
			if (value.equals(Boolean.TRUE)) {
				m_removed.put(vertex, null);
			} else {
				m_removed.remove(vertex);
			}
			return true;
		}
		return false;
	}
}
