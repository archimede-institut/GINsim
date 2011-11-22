package org.ginsim.graph.dynamicgraph;

import java.util.List;

import org.ginsim.graph.common.GraphAssociation;
import org.ginsim.graph.common.Edge;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.gui.service.tool.dynamicalhierarchicalsimplifier.NodeInfo;

/**
 * the dynamic (state transition) graph.
 */
public interface DynamicGraph extends Graph<DynamicNode, Edge<DynamicNode>>, GraphAssociation<RegulatoryGraph, RegulatoryNode, RegulatoryMultiEdge>{


	
	/**
	 * add an edge between source and target
	 * @param source
	 * @param target
	 * @param multiple
	 * @return the new edge
	 */
	public Edge<DynamicNode> addEdge(DynamicNode source, DynamicNode target, boolean multiple);
	
	
	/**
	 * Return the node order as a list of String
	 * 
	 * @return the node order as a list of String
	 */
	public List<NodeInfo> getNodeOrder();
	
	
	/**
	 * Set a list of String representing the order of node as defined by the model
	 * 
	 * @param list the list of String representing the order of node as defined by the model
	 */
	public void setNodeOrder( List<NodeInfo> node_order);
	
	
    /**
     * look for the shortest path between two states.
     * @param source
     * @param target
     * @return the List describing the path or null if none is found
     */
    public List<Edge> shortestPath(byte[] source, byte[] target) ;
}
