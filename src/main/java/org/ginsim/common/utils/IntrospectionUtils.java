package org.ginsim.common.utils;

import java.util.ArrayList;
import java.util.List;

import org.ginsim.core.graph.Graph;

/**
 * Collection of static methods for common introspection tasks.
 * The main function is to guess the type of Graph implemented by an object.
 *
 * @author Lionel Spinelli
 * @author Aurelien Naldi
 */
public class IntrospectionUtils {

	/**
	 * Return the deeper interface that inherits from Graph interface
	 *
	 * @param classe
	 * @return
	 */
    public static Class getGraphInterface( Class classe) {

        Class[] interfaces = classe.getInterfaces();
        if( interfaces.length != 0){
	        for (int i = 0; i < interfaces.length; i++) {
	            List<Class> all_interfaces = new ArrayList<Class>();
	            all_interfaces.add( interfaces[i]);
	            all_interfaces.addAll( getSuperInterfaces( interfaces[i]));
	            if( all_interfaces.contains( Graph.class)){
	            	return interfaces[i];
	            }
	        }
        }

    	Class super_class =  classe.getSuperclass();
    	if( super_class != null){
    		return getGraphInterface( super_class);
    	}


        return null;
    }

    /**
     * Return the list of all the interfaces (recursively) the given class implements
     *
     * @param classe
     * @return
     */
    public static List<Class> getSuperInterfaces( Class classe) {

        List<Class> allInterfaces = new ArrayList<Class>();

        Class[] interfaces = classe.getInterfaces();
        for (int i = 0; i < interfaces.length; i++) {
            allInterfaces.add( interfaces[i]);
            allInterfaces.addAll( getSuperInterfaces( interfaces[i]));
        }

        return allInterfaces;
    }
}
