package org.ginsim.common.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Collection of static methods for common introspection tasks.
 * The main function is to guess the type of Graph implemented by an object.
 *
 * @author Lionel Spinelli
 * @author Aurelien Naldi
 */
public class IntrospectionUtils {

	/**
	 * Return the deeper interface that inherits from the base interface
	 *
	 * @param classe
	 * @return
	 */
    public static Class getChildInterface( Class classe, Class baseclass) {

        Class[] interfaces = classe.getInterfaces();
        if( interfaces.length != 0){
	        for (int i = 0; i < interfaces.length; i++) {
	            List<Class> all_interfaces = new ArrayList<Class>();
	            all_interfaces.add( interfaces[i]);
	            all_interfaces.addAll( getSuperInterfaces( interfaces[i]));
	            if( all_interfaces.contains( baseclass)){
	            	return interfaces[i];
	            }
	        }
        }

    	Class super_class =  classe.getSuperclass();
    	if( super_class != null){
    		return getChildInterface( super_class, baseclass);
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
