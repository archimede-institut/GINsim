package org.ginsim.graph.hierachicaltransitiongraph;

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

import org.ginsim.exception.GsException;
import org.ginsim.graph.GraphManager;
import org.ginsim.graph.common.AbstractDerivedGraph;
import org.ginsim.graph.common.GraphAssociation;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.NodeAttributesReader;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.gui.service.tool.decisionanalysis.DecisionOnEdge;
import org.ginsim.gui.service.tool.dynamicalhierarchicalsimplifier.NodeInfo;
import org.ginsim.gui.service.tool.reg2dyn.SimulationParameters;
import org.ginsim.gui.shell.editpanel.AbstractParameterPanel;
import org.ginsim.io.parser.GinmlHelper;

import fr.univmrs.tagc.common.managerresources.Translator;
import fr.univmrs.tagc.common.xml.XMLWriter;

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
