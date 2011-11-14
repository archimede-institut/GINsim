package org.ginsim.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;


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
	private HashMap<Class<GsService>, GsService> services = new HashMap<Class<GsService>, GsService>();
	
	
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
	 * Factory creator. Instantiate the manager and ask the ServiceLoader to load the GsService list.
	 * 
	 */
	private GsServiceManager(){
		
		
        Iterator<GsService> service_list = ServiceLoader.load( GsService.class).iterator(); 
        while (service_list.hasNext()) {
            try {
            	GsService service = service_list.next();
            	if( service != null){
            		services.put( (Class<GsService>) service.getClass(), service);
            	}
            }
            catch (ServiceConfigurationError e){

            }
        }
	}
	
	/**
	 * Give access to the list of available GsServices
	 * 
	 * @return a List of available GsServices
	 */
	public Set<Class<GsService>> getAvailableServices(){
		
		return services.keySet();
	}
	
	
	/**
	 * Give access to the service 
	 * 
	 * @param service_class
	 * @return
	 */
	public GsService getService( Class<GsService> service_class){
		
		return services.get( service_class);
	}
	
}
