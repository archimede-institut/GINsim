package org.ginsim.graph.regulatorygraph;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.filechooser.FileFilter;

import org.ginsim.annotation.BiblioManager;
import org.ginsim.exception.GsException;
import org.ginsim.exception.NotificationMessage;
import org.ginsim.exception.NotificationMessageAction;
import org.ginsim.exception.NotificationMessageHolder;
import org.ginsim.graph.common.AbstractGraph;
import org.ginsim.graph.common.Edge;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.EdgeAttributesReader;
import org.ginsim.graph.common.VertexAttributesReader;
import org.ginsim.graph.dynamicgraph.DynamicGraph;
import org.ginsim.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.graph.regulatorygraph.omdd.OMDDNode;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.regulatorygraph.mutant.MutantListManager;
import org.ginsim.gui.shell.GsFileFilter;
import org.ginsim.io.parser.GinmlHelper;

import fr.univmrs.tagc.common.managerresources.Translator;
import fr.univmrs.tagc.common.xml.XMLWriter;

/**
 * The regulatory graph
 */
public interface RegulatoryGraph extends Graph<RegulatoryVertex, RegulatoryMultiEdge>, NotificationMessageHolder{
	
    /**
     * Return the node order
     * 
     * @return the node order as a list of RegulatoryVertex
     */
    public List<RegulatoryVertex> getNodeOrder();
    
    /**
     * add a vertex from textual parameters (for the parser).
     *
     * @param id
     * @param name
     * @param max
     * @return the new vertex.
     */
    public RegulatoryVertex addNewVertex(String id, String name, byte max);
    
    /**
     * 
     * @return
     */
    public RegulatoryVertex addVertex();
    
    
    /**
     * 
     * @param newId
     * @return True if a vertex of the graph has the given ID
     */
    public boolean idExists(String newId);
    
    /**
     * add an edge from textual parameters (for the parser).
     * @param from
     * @param to
     * @param minvalue
     * @param maxvalue
     * @param sign
     * @return the new edge.
     */
    public RegulatoryEdge addNewEdge(String from, String to, byte minvalue, byte sign) throws GsException;
    
    /**
     * add an edge from textual parameters (for the parser).
     * @param from
     * @param to
     * @param minvalue
     * @param maxvalue
     * @param sign
     * @return the new edge
     */
    public RegulatoryEdge addNewEdge(String from, String to, byte minvalue, String sign)  throws GsException;
    
    
    /**
     * Add a signed edge
     * 
     * @param source
     * @param target
     * @param sign
     * @return
     */
    public RegulatoryMultiEdge addEdge(RegulatoryVertex source, RegulatoryVertex target, int sign);
    
    
    /**
     * @param focal if true, leaves are focal points. Otherwise their are directions (-1, 0, +1)
     * @return a tree representation of logical parameters
     */
    public OMDDNode[] getAllTrees(boolean focal);
    
    
    /**
     * 
     * @param focal
     * @return
     */
	public OMDDNode[] getParametersForSimulation(boolean focal);
	
    
    /**
     * 
     * @param vertex
     * @param newId
     * @throws GsException
     */
    public void changeVertexId(Object vertex, String newId) throws GsException;
    
    /**
     * 
     * @return
     */
	public List getNodeOrderForSimulation();
	
	
	/**
	 * Set a list of class dependent objects representing the order of vertex as defined by the model
	 * 
	 * @param list the list of objects representing the order of vertex as defined by the model
	 */
	public void setNodeOrder( List<RegulatoryVertex> list);
	
	
    /**
     * 
     * @param vertex
     * @param newMax
     * @param l_fixable
     * @param l_conflict
     */
	public void canApplyNewMaxValue(RegulatoryVertex vertex, byte newMax, List l_fixable, List l_conflict);
    
}
