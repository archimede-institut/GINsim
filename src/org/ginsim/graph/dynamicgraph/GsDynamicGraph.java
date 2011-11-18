package org.ginsim.graph.dynamicgraph;

import java.util.List;

import org.ginsim.graph.common.GraphAssociation;
import org.ginsim.graph.common.Edge;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.regulatorygraph.GsRegulatoryGraph;
import org.ginsim.graph.regulatorygraph.GsRegulatoryMultiEdge;
import org.ginsim.graph.regulatorygraph.GsRegulatoryVertex;
import org.ginsim.gui.service.tools.dynamicalhierarchicalsimplifier.NodeInfo;

/**
 * the dynamic (state transition) graph.
 */
public interface GsDynamicGraph extends Graph<GsDynamicNode, Edge<GsDynamicNode>>, GraphAssociation<GsRegulatoryGraph, GsRegulatoryVertex, GsRegulatoryMultiEdge>{


	
	/**
	 * add an edge between source and target
	 * @param source
	 * @param target
	 * @param multiple
	 * @return the new edge
	 */
	public Edge<GsDynamicNode> addEdge(GsDynamicNode source, GsDynamicNode target, boolean multiple);
	
	
	/**
	 * Return the node order as a list of String
	 * 
	 * @return the node order as a list of String
	 */
	public List<NodeInfo> getNodeOrder();
	
	
	/**
	 * Set a list of String representing the order of vertex as defined by the model
	 * 
	 * @param list the list of String representing the order of vertex as defined by the model
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
