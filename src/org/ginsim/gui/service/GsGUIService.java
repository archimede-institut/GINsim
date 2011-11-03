package org.ginsim.gui.service;

import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.Graph;

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

public interface GsGUIService {

	/**
	 * Provide actions for a given graph.
	 * 
	 * It should add to the actions list, the actions that this service provides for the given graph.
	 * These actions can be divided into four types:
	 *  - Import : data import from file of various format. Represented by the GsImportAction class
	 *  - Export : data export to file of various format. Represented by the GsExportAction class
	 *  - Layout : Perform a graph layout. Represented by the GsLayoutAction class
	 *  - Action : various data management algorithm. Represented by the GsMiscAction class
	 * 
	 * @return the served graph class
	 */
	public void registerActions(List<Action> actions, Graph<?, ?> graph);
	
}
