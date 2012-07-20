package org.ginsim.core.graph.hierachicaltransitiongraph;

import java.util.List;
import java.util.Map;

import org.colomoto.logicalmodel.NodeInfo;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.GraphAssociation;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;


public interface HierarchicalTransitionGraph extends Graph<HierarchicalNode, DecisionOnEdge>, GraphAssociation<RegulatoryGraph, RegulatoryNode, RegulatoryMultiEdge>{

	
	/**
	 * add an edge between source and target
	 * @param source a HierarchicalNode
	 * @param target a HierarchicalNode
	 * @return the new edge
	 */
	public Object addEdge(HierarchicalNode source, HierarchicalNode target);
	
	/**
	 * 
	 * Return the Node with the given state
	 * 
	 * @param state
	 * @return the Node with the given state
	 */
	public HierarchicalNode getNodeForState(byte[] state);
	
	
	/**
	 * Set a list of NodeInfo representing the order of node as defined by the model
	 * 
	 * @param list the list of NodeInfo representing the order of node as defined by the model
	 */
	public void setNodeOrder( List<NodeInfo> node_order);
	
	
	/**
	 * Return the node order as a List of NodeInfo
	 * 
	 * @return the node order as a List of NodeInfo
	 */
	public List<NodeInfo> getNodeOrder();
	
	
	/**
	 * @return an array indicating for each node in the nodeOrder their count of childs. (ie. their max value)
	 */
	public byte[] getChildsCount();
	
	
	/**
	 * Set the array indicating for each node in the nodeOrder their count of childs. (ie. their max value)
	 * 
	 * @param cc the array indicating for each node in the nodeOrder their count of childs. (ie. their max value)
	 */
	
	public void setChildsCount(byte[] cc);
	
	/**
	 * Set the mode
	 * 
	 * @param mode
	 */
	public void setMode(int mode);
	
	
	/**
	 * Return <b>true</b> if the transients are compacted into component by their atteignability of attractors.
	 * 
	 * @return <b>true</b> if the transients are compacted into component by their atteignability of attractors.
	 */
	public boolean areTransientCompacted();

}
