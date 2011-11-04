package org.ginsim.service;

import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Vector;

import java.lang.Class;

import org.ginsim.graph.Graph;


/**
 * This manager provides access to the GsServices corresponding to a specific graph class
 * The manager itself is managed as a singleton.
 * The instances of GsServices are managed through a List. Their instances are generated using 
 *  the Service Provider interface implying each GsServices extension have to declare 
 *  the annotation "@ProviderFor(GsService.class)"
 * 
 * A user may access statically to the manager singleton through the getFactory method and then call the
 * 	getAvailableServices method to obtain the List of suitable GsServices
 * 
 * 
 * @author Lionel Spinelli
 */


public class GsServiceManager{

	// The manager singleton
	private static GsServiceManager manager = null;
	
	// The map establishing the correspondence between graph class and GraphGUIHelper instance
	private List<GsService> services = new Vector<GsService>();
	

	/**
	 * Factory creator. Instantiate the manager and ask the ServiceLoader to load the GsService list.
	 * 
	 */
	private GsServiceManager(){
		
		
        Iterator<GsService> service_list = ServiceLoader.load( GsService.class).iterator(); 
        while (service_list.hasNext()) {
            try {
            	GsService service = service_list.next();
            	if( service != null){
            		services.add( service);
            	}
            }
            catch (ServiceConfigurationError e){
                    // For now just ignore the exceptions
            }
        }
	}
	
	
	/**
	 * Method providing access to the manager instance
	 * 
	 * @return the GsServiceManager singleton 
	 */
	public static GsServiceManager getManager(){
		
		if( manager == null){
			manager = new GsServiceManager();
		}
		
		return manager;
	}
	
	/**
	 * Give access to the list of GsService providing services to the given graph
	 * 
	 * @param graph The graph for which the requested service are providing services
	 * @return a List of GsService providing service to the given graph.
	 */
	public List<GsService> getAvailableServices( Graph<?,?> graph){
		
		List<GsService> result = new Vector<GsService>();
		
		Class<?>[] graph_interfaces = graph.getClass().getInterfaces();
		Iterator<GsService> service_ite = services.iterator();
		while( service_ite.hasNext()) {
			GsService service = (GsService) service_ite.next();
			Class<?> served_graph = service.getServedGraphClass();
			for(int i = 0; i < graph_interfaces.length; i++){
				if( graph_interfaces[i].getName().equals( served_graph.getName())){
					result.add( service);			
				}
			}
		}
		
		return result;
	}
	
}
