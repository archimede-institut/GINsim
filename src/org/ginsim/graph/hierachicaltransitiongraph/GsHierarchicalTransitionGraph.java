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
import org.ginsim.graph.common.AbstractAssociatedGraphFrontend;
import org.ginsim.graph.common.AssociatedGraph;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.VertexAttributesReader;
import org.ginsim.graph.regulatorygraph.GsRegulatoryGraph;
import org.ginsim.graph.regulatorygraph.GsRegulatoryMultiEdge;
import org.ginsim.graph.regulatorygraph.GsRegulatoryVertex;
import org.ginsim.gui.service.tools.decisionanalysis.GsDecisionOnEdge;
import org.ginsim.gui.service.tools.dynamicalhierarchicalsimplifier.NodeInfo;
import org.ginsim.gui.service.tools.reg2dyn.GsSimulationParameters;
import org.ginsim.gui.shell.editpanel.AbstractParameterPanel;

import fr.univmrs.tagc.GINsim.xml.GsGinmlHelper;
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

public interface GsHierarchicalTransitionGraph extends Graph<GsHierarchicalNode, GsDecisionOnEdge>, AssociatedGraph<GsRegulatoryGraph, GsRegulatoryVertex, GsRegulatoryMultiEdge>{

	
	/**
	 * add an edge between source and target
	 * @param source a GsHierarchicalNode
	 * @param target a GsHierarchicalNode
	 * @return the new edge
	 */
	public Object addEdge(GsHierarchicalNode source, GsHierarchicalNode target);
	
	/**
	 * 
	 * Return the Vertex with the given state
	 * 
	 * @param state
	 * @return the Vertex with the given state
	 */
	public GsHierarchicalNode getNodeForState(byte[] state);
	
	
	/**
	 * Set a list of NodeInfo representing the order of vertex as defined by the model
	 * 
	 * @param list the list of NodeInfo representing the order of vertex as defined by the model
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
