package org.ginsim.gui.service;

import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.Graph;

/**
 * This interface is the central interface for GUIs for GINsim services.
 * GUI services provide Actions depending on the type of graph.
 *  
 *  Each Service should declare the annotation "@MetaInfServices(GsGUIService.class)"
 * 
 * @author Lionel Spinelli
 * @author Aurelien Naldi
 * @author Duncan Berenguier
 *
 */
public interface ServiceGUI {

	/**
	 * Weight for menu Graph, action affecting the selection
	 */
	public static final int W_GRAPH_SELECTION = 0;
	/**
	 * Weight for menu Graph, action affecting the graphicalAttributes
	 */
	public static final int W_GRAPH_COLORIZE = 500;
	
	/**
	 * Weight for menu Tools, action like simulation, stablestates...
	 */
	public static final int W_TOOLS_MAIN = 5000;

	/**
	 * Weight for menu Toolits, all
	 */
	public static final int W_TOOLKITS_MAIN = 10000;
	
	/**
	 * Extra weight for UNDER_DEVELOPMENT actions
	 */
	public static final int W_UNDER_DEVELOPMENT = 100000;

	/**
	 * Weight for menu export, images/doc
	 */
	public static final int W_EXPORT_DOC = 15000;
	/**
	 * Weight for menu export, generic graph viz
	 */
	public static final int W_EXPORT_GENERIC = 15500;
	/**
	 * Weight for menu export, specific file types
	 */
	public static final int W_EXPORT_SPECIFIC = 16000;


	/**
	 * An array of the separator
	 * The desired seprators must be ordered by weight
	 */
	public static final int[] separators = {W_GRAPH_COLORIZE, W_EXPORT_GENERIC, W_EXPORT_SPECIFIC,  W_UNDER_DEVELOPMENT+W_TOOLS_MAIN, W_UNDER_DEVELOPMENT+W_TOOLKITS_MAIN};
	
	
	
	/**
	 * Give access to the list of the actions that the service provides for the given graph.
	 * These actions can be divided into four types:
	 *  - Import : data import from file of various format. Represented by the ImportAction class
	 *  - Export : data export to file of various format. Represented by the ExportAction class
	 *  - Layout : Perform a graph layout. Represented by the LayoutAction class
	 *  - Action : various data management algorithm. Represented by the GsMiscAction class
	 * 
	 * @return the list of actions provided by the service to the given graph type
	 */
	public List<Action> getAvailableActions(Graph<?, ?> graph);

	/**
	 * Give a weight for the service, as defined by the developer.
	 * 
	 * @return a positive weight or -1 if unsure
	 */
	public int getInitialWeight();
	
	/**
	 * Give the real weight for the service.
	 * 
	 * @return a positive weight or -1 if unsure
	 */
	public int getWeight();

	/**
	 * define the new weight of the service
	 * @param new_weight
	 */
	public void setWeight(int new_weight);

}

