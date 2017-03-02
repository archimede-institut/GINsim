package org.ginsim.core.service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.Set;

import org.colomoto.biolqm.services.ExtensionLoader;
import org.ginsim.common.application.LogManager;


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
 * @author Aurelien Naldi
 */
public class ServiceManager{

	// The manager singleton
	private static ServiceManager manager = null;
	
	// The map establishing the correspondence between graph class and GraphGUIHelper instance
	private HashMap<Class<Service>, Service> services = new HashMap<Class<Service>, Service>();
	private HashMap<String, Service> serviceNames = new HashMap<String, Service>();
	
	
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
	 * Method providing access to the LQM manager instance
	 * 
	 * @return the ServiceManager singleton 
	 */
	public static org.colomoto.biolqm.services.ServiceManager getLQMManager(){
		return org.colomoto.biolqm.services.ServiceManager.getManager();
	}

	/**
	 * Factory creator. Instantiate the manager and ask the ServiceLoader to load the Service list.
	 * 
	 */
	private ServiceManager(){
		
		
        Iterator<Service> service_list = ExtensionLoader.iterator( Service.class);
        while (service_list.hasNext()) {
            try {
            	Service service = service_list.next();
            	if( service != null){
            		Class<Service> cl = (Class<Service>) service.getClass();
            		services.put( cl, service);
            		if (serviceNames.containsKey(cl.getName())) {
            			LogManager.error("Duplicated service:" + cl.getName());
            		} else {
            			serviceNames.put(cl.getName(), service);
            		}
            		Alias alias = cl.getAnnotation(Alias.class);
            		if (alias != null) {
                		if (serviceNames.containsKey(alias.value())) {
                			LogManager.error("Duplicated service:" + alias.value());
                		} else {
                    		serviceNames.put(alias.value(), service);
                		}
            		}
            	}
            }
            catch (ServiceConfigurationError e){
            	LogManager.debug(e);
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
	 * Give access to the service by service class
	 * 
	 * @param service_class
	 * @return the service or null if not found
	 */
	public <S extends Service> S getService( Class<S> service_class){
		
		return (S)services.get( service_class);
	}

	/**
	 * Give access to the service by service name 
	 * 
	 * @param name
	 * @return the service or null if not found
	 */
	public Service getService( String name){
		
		return serviceNames.get( name);
	}

	/**
	 * Get a service directly, i.e. shortcut for getManager().getService( service_class)
	 * 
	 * @param service_class
	 * @return the service or null if not found
	 */
	public static <S extends Service> S get( Class<S> service_class) {
		return getManager().getService( service_class);
	}
	
	/**
	 * Get a service directly, i.e. shortcut for getManager().getService( name)
	 * 
	 * @param name
	 * @return the service or null if not found
	 */
	public static Service get( String name) {
		return getManager().getService( name);
	}

    public ServiceClassInfo[] getServicesInfo() {
        ServiceClassInfo[] ret = new ServiceClassInfo[services.size()];
        int idx = 0;
        for (Class<Service> cl: services.keySet()) {
            ret[idx++] = new ServiceClassInfo(cl);
        }
        return ret;
    }
}
