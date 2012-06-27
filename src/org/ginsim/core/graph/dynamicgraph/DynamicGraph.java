package org.ginsim.core.graph.dynamicgraph;

import java.util.List;

import org.colomoto.logicalmodel.NodeInfo;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.GraphAssociation;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;

/**
 * the dynamic (state transition) graph.
 */
public interface DynamicGraph extends Graph<DynamicNode, Edge<DynamicNode>>, GraphAssociation<RegulatoryGraph, RegulatoryNode, RegulatoryMultiEdge>{

	public static final int MAXLEVEL = 9;
	public static final int STARLEVEL = MAXLEVEL+1;

	
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
