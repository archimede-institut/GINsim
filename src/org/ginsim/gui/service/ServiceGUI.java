package org.ginsim.gui.service;

import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.common.Graph;

/**
 * This interface is the central interface for GUIs for GINsim services.
 * GUI services provide Actions depending on the type of graph.
 *  
 *  Each Service should declare the annotation "@ProviderFor(GsGUIService.class)"
 * 
 * @author Lionel Spinelli
 * @author Aurelien Naldi
 *
 */
public interface ServiceGUI {

	/**
	 * Base weight for generic actions: CFC, path search...
	 */
	public static final int W_GENERIC = 0;
	/**
	 * Base weight for main action: simulation
	 */
	public static final int W_MAIN = 100;
	/**
	 * Base weight for analysis tools: circuits, stable states
	 */
	public static final int W_ANALYSIS = 200;
	/**
	 * Base weight for manipulation tools: reduction
	 */
	public static final int W_MANIPULATION = 300;
	/**
	 * Base weight for informative tools: interaction functionality, color state, view logical functions...
	 */
	public static final int W_INFO = 400;
	
	
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
	 * Give a weight for the service.
	 * 
	 * "Categories" of service should add a weight based on groups defined as constant here.
	 * 
	 * @return a positive weight or -1 if unsure
	 */
	public int getWeight();

}

