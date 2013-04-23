package org.ginsim.core.graph.hierachicaltransitiongraph;

import java.io.IOException;

import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.io.parser.GINMLWriter;

public class HierarchicalGINMLWriter extends GINMLWriter<HierarchicalTransitionGraph, HierarchicalNode, DecisionOnEdge> {

	
	private final int mode;
	private final String str_no;
	private long saveEdgeID = 1;
	
	public HierarchicalGINMLWriter(HierarchicalTransitionGraph graph, int mode, String str_no) {
		super(graph);
		this.mode = mode;
		this.str_no = str_no;
	}

	@Override
	public String getGraphClassName() {
		return HierarchicalTransitionGraphFactory.KEY;
	}

	@Override
	public void hook_graphAttribute(XMLWriter out) throws IOException {
		out.addAttr("isCompact", ""+mode);
		out.addAttr("nodeorder", str_no);
	}

	@Override
	public void hook_nodeAttribute(XMLWriter out, HierarchicalNode node) throws IOException {
		out.addAttr("id", "s"+node.getUniqueId());
		
		addAttributeTag(out, "type", "string", node.typeToString());
		addAttributeTag(out, "states", "string", node.write().toString());
	}

	
	@Override
	public void hook_edgeAttribute(XMLWriter out, DecisionOnEdge edge) throws IOException {
		long source = edge.getSource().getUniqueId();
		long target = edge.getTarget().getUniqueId();
		out.addAttr("id", "e"+saveEdgeID++);
		out.addAttr("from", "s"+source);
		out.addAttr("to", "s"+target);
	}

}
