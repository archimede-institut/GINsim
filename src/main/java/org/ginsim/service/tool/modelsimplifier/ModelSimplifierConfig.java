package org.ginsim.service.tool.modelsimplifier;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ginsim.common.xml.XMLWriter;
import org.ginsim.common.xml.XMLize;
import org.ginsim.core.annotation.Annotation;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.utils.data.MultiColHelper;
import org.ginsim.core.utils.data.NamedObject;



public class ModelSimplifierConfig implements NamedObject, XMLize, MultiColHelper<RegulatoryNode> {
	private String name;
	Annotation note = new Annotation();
	Map<RegulatoryNode, List<RegulatoryNode>> m_removed = new HashMap<RegulatoryNode, List<RegulatoryNode>>();
	public boolean strict = true;

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
	public void toXML(XMLWriter out)
			throws IOException {
		if (m_removed.size() < 1) {
			return;
		}
		String s_removed = "";
		for (RegulatoryNode v: m_removed.keySet()) {
			s_removed += " "+v;
		}
		out.openTag("simplificationConfig");
		out.addAttr("name", this.name);
		out.addAttr("strict", ""+this.strict);
		out.addAttr("removeList", s_removed.substring(1));
		note.toXML(out);
		out.closeTag();
	}
	
	@Override
	public Object getVal(RegulatoryNode o, int index) {
		if (index == 1) {
			return m_removed.containsKey(o) ? Boolean.TRUE : Boolean.FALSE;
		}
		return o;
	}
	@Override
	public boolean setVal(RegulatoryNode vertex, int index, Object value) {
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
	
	public void remove(RegulatoryNode vertex) {
		m_removed.put(vertex, null);
	}
}
