package org.ginsim.core.graph.regulatorygraph;

import java.io.IOException;
import java.util.ArrayList;

import org.colomoto.biolqm.NodeInfo;
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
        
        // new ginml annotations
        if (graph instanceof RegulatoryGraph) {
        	NodeInfo nodeInfo = node.getNodeInfo();
			try {
				Metadata nodeMetadata = ((RegulatoryGraph) graph).getAnnotationModule().getMetadataOfNode(nodeInfo);
				String nodeNotes = nodeMetadata.getNotes();
				ArrayList<String> nodeResources = nodeMetadata.getListOfResources();
				annotationsToXML(out, nodeNotes, nodeResources);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	protected void hook_edgeAttribute(XMLWriter out, RegulatoryMultiEdge edge) throws IOException {
        edge.toXML(out);
        
        // new ginml annotations
        if (graph instanceof RegulatoryGraph) {
        	RegulatoryNode interNode1 = (RegulatoryNode) edge.getSource();
        	NodeInfo node1 = interNode1.getNodeInfo();
        	
        	RegulatoryNode interNode2 = (RegulatoryNode) edge.getTarget();
        	NodeInfo node2 = interNode2.getNodeInfo();
        	
        	try {
				Metadata edgeMetadata = ((RegulatoryGraph) graph).getAnnotationModule().getMetadataOfEdge(node1, node2);
				String edgeNotes = edgeMetadata.getNotes();
				ArrayList<String> edgeResources = edgeMetadata.getListOfResources();
				annotationsToXML(out, edgeNotes, edgeResources);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
