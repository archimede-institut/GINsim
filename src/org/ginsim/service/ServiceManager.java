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
 *  the annotation "@ProviderFor(Service.class)"
 * 
 * A user may access statically to the manager singleton through the getFactory method and then call the
 * 	getAvailableServices method to obtain the List of suitable GsServices
 * 
 * 
 * @author Lionel Spinelli
 */


public class ServiceManager{

	// The manager singleton
	private static ServiceManager manager = null;
	
	// The map establishing the correspondence between graph class and GraphGUIHelper instance
	private HashMap<Class<Service>, Service> services = new HashMap<Class<Service>, Service>();
	
	
	/**
	 * Method providing access to the manager instance
	 * 
	 * @return the ServiceManager singleton 
	 */
	public static ServiceManager getManager(){
		
		if( manager == null){
			manager = new ServiceManager();
		}
		
		return manager;
	}

	/**
	 * Factory creator. Instantiate the manager and ask the ServiceLoader to load the Service list.
	 * 
	 */
	private ServiceManager(){
		
		
        Iterator<Service> service_list = ServiceLoader.load( Service.class).iterator(); 
        while (service_list.hasNext()) {
            try {
            	Service service = service_list.next();
            	if( service != null){
            		services.put( (Class<Service>) service.getClass(), service);
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
	public Set<Class<Service>> getAvailableServices(){
		
		return services.keySet();
	}
	
	
	/**
	 * Give access to the service 
	 * 
	 * @param service_class
	 * @return
	 */
	public <S extends Service> S getService( Class<S> service_class){
		
		return (S)services.get( service_class);
	}

	/**
	 * Get a service directly, i.e. shortcut for getManager().getService( service_class)
	 * 
	 * @param service_class
	 * @return
	 */
	public static <S extends Service> S get( Class<S> service_class) {
		return getManager().getService( service_class);
	}
}
