package org.ginsim.core.service;

import java.util.HashMap;
import java.util.Set;

import org.colomoto.biolqm.ExtensionLoader;
import org.colomoto.biolqm.LQMServiceManager;
import org.ginsim.common.application.LogManager;
import org.ginsim.core.graph.GINsimFormat;


/**
 * This manager provides access to the GsServices corresponding to a specific graph class
 * The manager itself is managed as a singleton.
 * The instances of GsServices are managed through a List. Their instances are generated using 
 *  the Service Provider interface implying each GsServices extension have to declare 
 *  the annotation "@MetaInfServices(Service.class)"
 * 
 * A user may access statically to the manager singleton through the getFactory method and then call the
 * 	getAvailableServices method to obtain the List of suitable GsServices
 * 
 * 
 * @author Lionel Spinelli
 * @author Aurelien Naldi
 */
public class GSServiceManager {

	// The manager singleton
	private static GSServiceManager manager = null;
	
	// The map establishing the correspondence between graph class and GraphGUIHelper instance
	private static HashMap<Class<Service>, Service> services = new HashMap<Class<Service>, Service>();
	private static HashMap<String, Service> serviceNames = new HashMap<String, Service>();
	
	/**
	 * Factory creator. Instantiate the manager and ask the ServiceLoader to load the Service list.
	 */
	static {
		
        for (Service service: ExtensionLoader.load_instances(Service.class)) {
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
        
        // Manually register the bioLQM services
        LQMServiceManager.register(new GINsimFormat());
	}
	
	/**
	 * Give access to the list of available GsServices
	 * 
	 * @return a List of available GsServices
	 */
	public static Set<Class<Service>> getAvailableServices(){
		return services.keySet();
	}
	
	
	/**
	 * Give access to the service by service class
	 * 
	 * @param service_class
	 * @return the service or null if not found
	 */
	public static <S extends Service> S getService( Class<S> service_class){
		return (S)services.get( service_class);
	}

	/**
	 * Give access to the service by service name 
	 * 
	 * @param name
	 * @return the service or null if not found
	 */
	public static Service getService( String name){
		return serviceNames.get( name);
	}

	/**
	 * Get a service directly, i.e. shortcut for getManager().getService( service_class)
	 * 
	 * @param service_class
	 * @return the service or null if not found
	 */
	public static <S extends Service> S get( Class<S> service_class) {
		return getService( service_class);
	}
	
	/**
	 * Get a service directly, i.e. shortcut for getManager().getService( name)
	 * 
	 * @param name
	 * @return the service or null if not found
	 */
	public static Service get( String name) {
		return getService( name);
	}

    public static ServiceClassInfo[] getServicesInfo() {
        ServiceClassInfo[] ret = new ServiceClassInfo[services.size()];
        int idx = 0;
        for (Class<Service> cl: services.keySet()) {
            ret[idx++] = new ServiceClassInfo(cl);
        }
        return ret;
    }
}
