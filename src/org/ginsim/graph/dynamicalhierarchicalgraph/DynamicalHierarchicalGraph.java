package org.ginsim.graph.dynamicalhierarchicalgraph;


import java.util.List;

import org.ginsim.graph.common.GraphAssociation;
import org.ginsim.graph.common.Edge;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.gui.service.tool.dynamicalhierarchicalsimplifier.NodeInfo;

public interface DynamicalHierarchicalGraph extends Graph<DynamicalHierarchicalNode, Edge<DynamicalHierarchicalNode>>, GraphAssociation<RegulatoryGraph, RegulatoryNode, RegulatoryMultiEdge>{
	
	
	/**
	 * add an edge between source and target
	 * @param source
	 * @param target
	 * @return the new edge
	 */
	public Edge<DynamicalHierarchicalNode> addEdge(DynamicalHierarchicalNode source, DynamicalHierarchicalNode target);
	
	
	/**
	 * @return an array indicating for each node in the nodeOrder their count of childs. (ie. their max value)
	 */
	public byte[] getChildsCount();
	
	/**
	 * Set an array indicating for each node in the nodeOrder their count of childs. (ie. their max value)
	 * 
	 * @param an array indicating for each node in the nodeOrder their count of childs. (ie. their max value)
	 */
	public void setChildsCount(byte[] cc) ;
	
	
	/**
	 * 
	 * @param sid a string representation of the id with a letter in first position eg. "s102"
	 * @return the node with the corresponding id. eg. 102.
	 */
	public DynamicalHierarchicalNode getNodeById(String sid);
	
	/**
	 * Return the node order as a List of NodeInfo
	 * 
	 * @return the node order as a List of NodeInfo
	 */
	public List<NodeInfo> getNodeOrder();
	
	
	/**
	 * Set a list of NodeInfo representing the order of node as defined by the model
	 * 
	 * @param list the list of nodeInfo representing the order of node as defined by the model
	 */
	public void setNodeOrder( List<NodeInfo> node_order);
}
