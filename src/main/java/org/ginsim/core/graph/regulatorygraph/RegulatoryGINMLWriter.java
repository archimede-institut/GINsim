package org.ginsim.core.graph.regulatorygraph;

import java.io.IOException;
import java.util.ArrayList;

import org.colomoto.biolqm.NodeInfo;
import org.colomoto.biolqm.metadata.Annotator;
import org.colomoto.biolqm.metadata.annotations.Metadata;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.io.parser.GINMLWriter;

/**
 * Save a Regulatory Graphs in GINML.
 * 
 * @author Aurelien Naldi
 */
public class RegulatoryGINMLWriter extends GINMLWriter<RegulatoryGraph, RegulatoryNode, RegulatoryMultiEdge> {

	public RegulatoryGINMLWriter(RegulatoryGraph graph) {
		super(graph, RegulatoryGraphFactory.KEY);
	}
	
	protected void hook_graphAttribute(XMLWriter out) throws IOException {
  		out.addAttr("nodeorder", stringNodeOrder(graph.getNodeOrder()));
	}
	
	protected void hook_nodeAttribute(XMLWriter out, RegulatoryNode node) throws IOException {
        node.toXML(out);
		NodeInfo ni = node.getNodeInfo();
		annotationsToXML(out, graph.getAnnotator().node(ni));
	}
	
	protected void hook_edgeAttribute(XMLWriter out, RegulatoryMultiEdge edge) throws IOException {
        edge.toXML(out);
		NodeInfo node1 = edge.getSource().getNodeInfo();
		NodeInfo node2 = edge.getTarget().getNodeInfo();
		annotationsToXML(out, graph.getAnnotator().edge(node1, node2));
	}
}
