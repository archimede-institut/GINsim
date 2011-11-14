package org.ginsim.gui.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;

import javax.swing.Action;

import org.ginsim.graph.common.Graph;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.StandaloneGUI;
import org.ginsim.service.GsService;
import org.ginsim.service.GsServiceManager;

import fr.univmrs.tagc.common.Debugger;


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
        
        Iterator<GsServiceGUI> service_list = ServiceLoader.load( GsServiceGUI.class).iterator(); 
        while (service_list.hasNext()) {
            try {
            	GsServiceGUI service = service_list.next();
            	if( service != null){
            		services.add( service);
            	}
            }
            catch (ServiceConfigurationError e){

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
	 * Give access to the list of GUI actions provided by the available services for the given graph type
	 * 
	 * 
	 * @param graph The graph for which the available services are providing actions
	 * @return a List of Action that can be performed.
	 */
	public List<Action> getAvailableActions( Graph<?,?> graph) {

		List<Action> result = new ArrayList<Action>();

		//Retrieve the available service on server side
		Set<Class<GsService>> server_services = GsServiceManager.getManager().getAvailableServices();
		
		// Parse the existing serviceGUI to detect the ones that must be used
		for( GsServiceGUI service: services) {
			// Check if the serviceGUI is related to a server service
			GUIFor guifor = service.getClass().getAnnotation( GUIFor.class);
			if( guifor != null){
				Class<GsService> guifor_class = (Class<GsService>) guifor.value();
				if (server_services.contains( guifor_class)) {
					try{
						List<Action> service_actions = service.getAvailableActions( graph);
						if( service_actions != null){
							result.addAll( service_actions);
						}
					}
					catch( Exception e){
						Debugger.log( "Service '" + service.getClass().getName() + "' cannot provide Actions due to an exception: " + e);
					}
				}
			}
			// If no server service correspond to the serviceGUI, check if it is a standalone serviceGUI
			else{
				StandaloneGUI standalone = service.getClass().getAnnotation( StandaloneGUI.class);
				if( standalone != null){
					try {
						List<Action> service_actions = service.getAvailableActions( graph);
						if( service_actions != null){
							result.addAll( service_actions);
						}
					} catch (Throwable e)  {
						Debugger.log("Could not create actions for service: "+service);
					}
				}
			}
		}

		return result;
	}

}
