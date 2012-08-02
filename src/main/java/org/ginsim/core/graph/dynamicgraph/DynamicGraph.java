package org.ginsim.core.graph.dynamicgraph;

import java.util.List;

import org.colomoto.logicalmodel.LogicalModel;
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
	Edge<DynamicNode> addEdge(DynamicNode source, DynamicNode target, boolean multiple);
	
	
	/**
	 * Return the node order as a list of String
	 * 
	 * @return the node order as a list of String
	 */
	List<NodeInfo> getNodeOrder();
	
	
	/**
	 * Set a list of String representing the order of node as defined by the model
	 * 
	 * @param list the list of String representing the order of node as defined by the model
	 */
	void setNodeOrder( List<NodeInfo> node_order);
	
	
    /**
     * look for the shortest path between two states.
     * @param source
     * @param target
     * @return the List describing the path or null if none is found
     */
    List<Edge> shortestPath(byte[] source, byte[] target) ;
    
    /**
     * Get the list of known extra components names.
     * These components have no explicitly assigned value in the STG,
     * but their values can be retrieved based on a given state.
     * 
     * @return the list of names, or null if none
     */
    String[] getExtraNames();
    
    /**
     * Retrieve the values for all extra components for a given state.
     * If the provided array to fill is null or of the wrong size, a new array will be created and returned.
     * Otherwise, extraValues will be filled and returned.
     * 
     * @param state
     * @param extraValues array in which to put the values.
     * 
     * @return extraValues properly filled or a new array
     */
    byte[] fillExtraValues(byte[] state, byte[] extraValues);


    /**
     * Associate a logicalModel with this STG, notably to retrieve extra values.
     * @param model
     */
	void setLogicalModel(LogicalModel model);
}
