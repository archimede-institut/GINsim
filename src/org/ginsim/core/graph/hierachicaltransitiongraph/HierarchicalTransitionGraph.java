package org.ginsim.core.graph.hierachicaltransitiongraph;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.exception.GsException;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.AbstractDerivedGraph;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.GraphAssociation;
import org.ginsim.core.graph.common.NodeInfo;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.io.parser.GinmlHelper;
import org.ginsim.gui.resource.Translator;
import org.ginsim.gui.shell.editpanel.AbstractParameterPanel;
import org.ginsim.servicegui.tool.decisionanalysis.DecisionOnEdge;
import org.ginsim.servicegui.tool.reg2dyn.SimulationParameters;


/* SUMMARY
 * 
 * **************** CONSTRUCTORS ************/	
/* **************** EDITION OF VERTEX AND EDGE ************/	
/* **************** SPECIFIC ACTIONS & CO ************/	
/* **************** PANELS ************/	
/* **************** SAVE ************/	
/* **************** NODE SEARCH ************/
/* **************** GETTER AND SETTERS ************/
/* **************** UNIMPLEMENTED METHODS ************/

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
