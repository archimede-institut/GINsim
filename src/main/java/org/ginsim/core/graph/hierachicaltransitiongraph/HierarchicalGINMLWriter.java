package org.ginsim.core.graph.hierachicaltransitiongraph;

import java.io.IOException;

import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.io.parser.GINMLWriter;

public class HierarchicalGINMLWriter extends GINMLWriter<HierarchicalTransitionGraph, HierarchicalNode, DecisionOnEdge> {

	
	private final boolean isCompact;
	private final String str_no;
	private long saveEdgeID = 1;
	
	public HierarchicalGINMLWriter(HierarchicalTransitionGraph graph, boolean isCompact, String str_no) {
		super(graph, HierarchicalTransitionGraphFactory.KEY);
		this.isCompact = isCompact;
		this.str_no = str_no;
	}

	@Override
	public void hook_graphAttribute(XMLWriter out) throws IOException {
		out.addAttr("nodeorder", str_no);
		addAttributeTag(out, "isCompact", isCompact);
	}

	@Override
	public void hook_nodeAttribute(XMLWriter out, HierarchicalNode node) throws IOException {
		out.addAttr("id", "s"+node.getUniqueId());
		
		addAttributeTag(out, "type", node.typeToString());
		addAttributeTag(out, "states", node.write());
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
