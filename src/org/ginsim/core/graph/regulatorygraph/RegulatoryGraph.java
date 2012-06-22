package org.ginsim.core.graph.regulatorygraph;

import java.util.List;

import org.colomoto.mddlib.MDDManager;
import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.NodeInfo;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;
import org.ginsim.core.logicalmodel.LogicalModel;



/**
 * The regulatory graph
 */
public interface RegulatoryGraph extends Graph<RegulatoryNode, RegulatoryMultiEdge>{
	
    /**
     * Return the node order
     * 
     * @return the node order as a list of RegulatoryNode
     */
    List<RegulatoryNode> getNodeOrder();
    
    /**
     * add a node from textual parameters (for the parser).
     *
     * @param id
     * @param name
     * @param max
     * @return the new node.
     */
    RegulatoryNode addNewNode(String id, String name, byte max);
    
    /**
     * 
     * @return
     */
    RegulatoryNode addNode();
    
    
    /**
     * 
     * @param newId
     * @return True if a node of the graph has the given ID
     */
    boolean idExists(String newId);
    
    /**
     * add an edge from textual parameters (for the parser).
     * @param from
     * @param to
     * @param minvalue
     * @param maxvalue
     * @param sign
     * @return the new edge.
     */
    RegulatoryEdge addNewEdge(String from, String to, byte minvalue, RegulatoryEdgeSign sign) throws GsException;
    
    /**
     * add an edge from textual parameters (for the parser).
     * @param from
     * @param to
     * @param minvalue
     * @param maxvalue
     * @param sign
     * @return the new edge
     */
    RegulatoryEdge addNewEdge(String from, String to, byte minvalue, String sign)  throws GsException;
    
    
    /**
     * Add a signed edge
     * 
     * @param source
     * @param target
     * @param sign
     * @return
     */
    RegulatoryMultiEdge addEdge(RegulatoryNode source, RegulatoryNode target, RegulatoryEdgeSign sign);
    
    
    /**
     * @param focal if true, leaves are focal points. Otherwise their are directions (-1, 0, +1)
     * @return a tree representation of logical parameters
     */
    OMDDNode[] getAllTrees(boolean focal);

    /**
     * Computes the tree representing the logical parameters, receiving an optional node ordering
     * (otherwise uses the one already defined in the regulatory graph)
     *  
     * @param focal if true, leaves are focal points. Otherwise their are directions (-1, 0, +1)
     * @return a tree representation of logical parameters
     */
    OMDDNode[] getAllTrees(List<RegulatoryNode> nodeOrder, boolean focal);

    
    /**
     * 
     * @param focal
     * @return
     */
	OMDDNode[] getParametersForSimulation(boolean focal);

	
    /**
     * Construct MDD representation of the logical functions of the nodes of this graph.
     * 
     * @param factory a MDDFactory associated with the nodes of this graph 
     * @return an array containing references to the MDD roots in the factory
     */
    int[] getMDDs(MDDManager factory);

    /**
     * Construct a MDDFactory associated to the nodes of this graph
     * @return a new MDDFactory.
     */
    MDDManager getMDDFactory();

    
    /**
     * 
     * @param node
     * @param newId
     * @throws GsException
     */
    void changeNodeId(Object node, String newId) throws GsException;
    
    /**
     * 
     * @return
     */
	List<RegulatoryNode> getNodeOrderForSimulation();
	
	
	/**
	 * Set a list of class dependent objects representing the order of node as defined by the model
	 * 
	 * @param list the list of objects representing the order of node as defined by the model
	 */
	void setNodeOrder( List<RegulatoryNode> list);
	
	
    /**
     * 
     * @param node
     * @param newMax
     * @param l_fixable
     * @param l_conflict
     */
	void canApplyNewMaxValue(RegulatoryNode node, byte newMax, List l_fixable, List l_conflict);

	/**
	 * Get a ready-to-be-used model (list of nodes and functions, no graph structure)
	 * 
	 * @return a model matching this RegulatoryGraph
	 */
	LogicalModel getModel();

	/**
	 * Get a list of lightweight objects representing the node order
	 * 
	 * @return the ordered list of minimal information on components
	 */
	List<NodeInfo> getNodeInfos();
    
}
