package org.ginsim.service.tool.modelsimplifier;

import java.io.IOException;
import java.util.*;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.NodeInfo;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.common.xml.XMLize;
import org.ginsim.core.annotation.Annotation;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.utils.data.NamedObject;



public class ModelSimplifierConfig implements NamedObject, XMLize {
	private String name;
	Annotation note = new Annotation();
	Set<NodeInfo> m_removed = new HashSet<NodeInfo>();
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
		for (NodeInfo v: m_removed) {
			s_removed += " "+v;
		}
		out.openTag("simplificationConfig");
		out.addAttr("name", this.name);
		out.addAttr("strict", ""+this.strict);
		out.addAttr("removeList", s_removed.substring(1));
		note.toXML(out);
		out.closeTag();
	}

    public boolean isSelected(NodeInfo node) {
        return m_removed.contains(node);
    }

	public void setSelected(NodeInfo node, boolean selected) {
        if (selected) {
            m_removed.add(node);
        } else {
            m_removed.remove(node);
        }
	}
	
	public void remove(RegulatoryNode vertex) {
		m_removed.add(vertex.getNodeInfo());
	}

    public LogicalModel apply(LogicalModel model) {
        ReductionTask task = new ReductionTask(model, this);
        try {
            return task.call();
        } catch (Exception e) {
            return null;
        }
    }
}
