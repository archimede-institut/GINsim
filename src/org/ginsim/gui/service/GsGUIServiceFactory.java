package org.ginsim.gui.service;

import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import javax.swing.Action;

import org.ginsim.graph.Graph;


/**
 * This factory provides access to the GsGUIServices corresponding to a specific graph class
 * The factory itself is managed as a singleton.
 * The instances of GsServices are managed through a List. Their instances are generated using 
 *  the Service Provider interface implying each GsServices extension have to declare 
 *  the annotation "@ProviderFor(GsGUIService.class)"
 * 
 * A user may access statically to the factory singleton through the getFactory method and then call the
 * 	getAvailableServices method to obtain the List of suitable GsServices
 * 
 * 
 * @author Lionel Spinelli
 * @author Aurelien Naldi
 */


public class GsGUIServiceFactory{

	// The factory singleton
	private static GsGUIServiceFactory factory = null;
	
	// The map establishing the correspondence between graph class and GraphGUIHelper instance
	private List<GsGUIService> services = new ArrayList<GsGUIService>();
	

	/**
	 * Factory creator. Instantiate the factory and ask the ServiceLoader to load the GsGUIService list.
	 * 
	 */
	private GsGUIServiceFactory(){
		
        for (GsGUIService service: ServiceLoader.load( GsGUIService.class)) {
        	if( service != null){
        		services.add( service);
        	}
        }
	}
	
	
	/**
	 * Method providing access to the factory instance
	 * 
	 * @return the GsServiceFactory singleton 
	 */
	public static GsGUIServiceFactory getFactory() {

		if( factory == null){
			factory = new GsGUIServiceFactory();
		}
		return factory;
	}
	
	/**
	 * Give access to the list of actions available for the given graph
	 * 
	 * @param graph The graph for which the requested service are providing services
	 * @return a List of Action that can be performed.
	 */
	public List<Action> getAvailableActions( Graph<?,?> graph) {

		List<Action> result = new ArrayList<Action>();
		for ( GsGUIService service: services) {
			service.registerActions(result, graph);
		}

		return result;
	}

}
