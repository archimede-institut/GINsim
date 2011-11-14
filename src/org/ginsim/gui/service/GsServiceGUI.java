package org.ginsim.gui.service;

import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.common.Graph;

/**
 * This interface is the central interface for GUIs for GINsim services.
 * GUI services provide Actions depending on the type of graph.
 *  
 *  Each Service must declare the annotation "@ProviderFor(GsGUIService.class)"
 * 
 * @author Lionel Spinelli
 * @author Aurelien Naldi
 *
 */

public interface GsServiceGUI {

	/**
	 * Give access to the list of the actions that the service provides for the given graph.
	 * These actions can be divided into four types:
	 *  - Import : data import from file of various format. Represented by the GsImportAction class
	 *  - Export : data export to file of various format. Represented by the GsExportAction class
	 *  - Layout : Perform a graph layout. Represented by the GsLayoutAction class
	 *  - Action : various data management algorithm. Represented by the GsMiscAction class
	 * 
	 * @return the list of actions provided by the service to the given graph type
	 */
	public List<Action> getAvailableActions(Graph<?, ?> graph);
	
}

