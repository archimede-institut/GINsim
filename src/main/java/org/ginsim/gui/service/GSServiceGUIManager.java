package org.ginsim.gui.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.Set;

import javax.swing.Action;

import org.colomoto.biolqm.ExtensionLoader;
import org.ginsim.Launcher;
import org.ginsim.common.application.LogManager;
import org.ginsim.core.service.ServiceClassInfo;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.service.EStatus;
import org.ginsim.core.service.Service;
import org.ginsim.core.service.GSServiceManager;
import org.ginsim.core.service.ServiceStatus;


/**
 * This manager provides access to the ServiceGUI corresponding to a specific graph class
 * The manager itself is managed as a singleton.
 * The instances of GsServiceGUIs are managed through a List. Their instances are generated using 
 *  the Service Provider interface implying each GsServices extension have to declare 
 *  the annotation "@MetaInfServices(ServiceGUI.class)"
 * 
 * A user may access statically to the manager singleton through the getFactory method and then call the
 * 	getAvailableServices method to obtain the List of suitable GsServices
 * 
 * 
 * @author Lionel Spinelli
 * @author Aurelien Naldi
 */
public class GSServiceGUIManager{

	// The map establishing the correspondence between graph class and GraphGUIHelper instance
	private static List<ServiceGUI> services = new ArrayList<ServiceGUI>();
	

	/**
	 * Factory creator. Instantiate the manager and ask the ServiceLoader to load the ServiceGUI list.
	 * 
	 */
	static {
        
        List<ServiceGUI> service_list = ExtensionLoader.load_instances(ServiceGUI.class);
        for (ServiceGUI service: service_list) {
			// Check for the service status. If service is not published, it is not
			// provided apart in case of a development environment
			ServiceStatus service_status = service.getClass().getAnnotation( ServiceStatus.class);
			EStatus status;
			if (service_status != null) {
				status = service_status.value();
			} else {
				LogManager.error( "Service '" + service.getClass().getName() + "' does not have a declared status. Consider it deprecated.");
				status = EStatus.DEPRECATED;
			}
			boolean rejected = true;
			switch (status) {
			case DEPRECATED:
				rejected = true;
				break;
			case DEVELOPMENT:
				rejected = !Launcher.developer_mode;
				service.setWeight(ServiceGUI.W_UNDER_DEVELOPMENT);
				break;
			case RELEASED:
				rejected = false;
				break;
			}
			
			if (!rejected) {
    			// Check the weight of the service so it can be ordered
            	int weight = service.getWeight();
            	int position = 0;
        		for (ServiceGUI s: services) {
        			if (s.getWeight() > weight) {
        				break;
        			}
        			position++;
        		}
        		
        		// Add the service to the list
        		services.add( position, service);
			}
        }
	}
	
	
	/**
	 * Give access to the list of GUI actions provided by the available services for the given graph type
	 * 
	 * 
	 * @param graph The graph for which the available services are providing actions
	 * @return a List of Action that can be performed.
	 */
	public static List<Action> getAvailableActions( Graph<?,?> graph) {

		List<Action> result = new ArrayList<Action>();

		//Retrieve the available service on server side
		Set<Class<Service>> server_services = GSServiceManager.getAvailableServices();

		// Parse the existing serviceGUI to detect the ones that must be used
		for( ServiceGUI service: services) {
			
			// Check if the serviceGUI is related to a server service
			GUIFor guifor = service.getClass().getAnnotation( GUIFor.class);
			if( guifor != null){
				Class<Service> guifor_class = (Class<Service>) guifor.value();
				if (server_services.contains( guifor_class)) {
					try{
						List<Action> service_actions = service.getAvailableActions( graph);

						if( service_actions != null){
							result.addAll( service_actions);
						}
					}
					catch( Throwable e){
						LogManager.error( "Service '" + service.getClass().getName() + "' cannot provide Actions due to an exception");
						LogManager.error( e);
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
						LogManager.error( "Could not create actions for service: "+service);
						LogManager.error( e);
					}
				}
			}
		}

		return result;
	}

    public static ServiceClassInfo[] getServicesInfo() {
        ServiceClassInfo[] ret = new ServiceClassInfo[services.size()];
        int idx = 0;
        for (ServiceGUI srv: services) {
            ret[idx++] = new ServiceClassInfo(srv.getClass());
        }
        return ret;
    }
}
