package org.ginsim.gui.service;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import javax.swing.Action;

import org.ginsim.graph.Graph;
import org.ginsim.service.GsService;
import org.ginsim.service.GsServiceManager;


/**
 * This manager provides access to the GsServiceGUI corresponding to a specific graph class
 * The manager itself is managed as a singleton.
 * The instances of GsServiceGUIs are managed through a List. Their instances are generated using 
 *  the Service Provider interface implying each GsServices extension have to declare 
 *  the annotation "@ProviderFor(GsServiceGUI.class)"
 * 
 * A user may access statically to the manager singleton through the getFactory method and then call the
 * 	getAvailableServices method to obtain the List of suitable GsServices
 * 
 * 
 * @author Lionel Spinelli
 * @author Aurelien Naldi
 */


public class GsServiceGUIManager{

	// The manager singleton
	private static GsServiceGUIManager manager = null;
	
	// The map establishing the correspondence between graph class and GraphGUIHelper instance
	private List<GsServiceGUI> services = new ArrayList<GsServiceGUI>();
	

	/**
	 * Factory creator. Instantiate the manager and ask the ServiceLoader to load the GsServiceGUI list.
	 * 
	 */
	private GsServiceGUIManager(){

        for (GsServiceGUI service: ServiceLoader.load( GsServiceGUI.class)) {
        	if( service != null){
        		services.add( service);
        	}
        }
	}
	
	
	/**
	 * Method providing access to the manager instance
	 * 
	 * @return the GsServiceGUIManager singleton 
	 */
	public static GsServiceGUIManager getManager() {

		if( manager == null){
			manager = new GsServiceGUIManager();
		}
		return manager;
	}
	
	/**
	 * Give access to the list of actions provided by the available services for the given graph type
	 * 
	 * @param graph The graph for which the available services are providing actions
	 * @return a List of Action that can be performed.
	 */
	public List<Action> getAvailableActions( Graph<?,?> graph) {

		List<Action> result = new ArrayList<Action>();
		
		List<GsService> server_services = GsServiceManager.getManager().getAvailableServices( graph);
		for( GsService server_service: server_services){
			for( GsServiceGUI service: services) {
				GUIFor gui_for = service.getClass().getAnnotation( GUIFor.class);
				Class<?> gui_for_class = gui_for.value();
				if (server_service.getClass().equals( gui_for_class)) {
					List<Action> service_actions = service.getAvailableActions( graph);
					result.addAll( service_actions);
				}
			}
	        
		}
		
//		for( GsServiceGUI service: services) {
//			List<Action> service_actions = service.getAvailableActions( graph);
//			result.addAll( service_actions);
//		}

		return result;
	}

}
