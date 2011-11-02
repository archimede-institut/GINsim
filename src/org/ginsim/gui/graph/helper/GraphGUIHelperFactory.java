package org.ginsim.gui.graph.helper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;

import org.ginsim.graph.Graph;

/**
 * This factory provides access to the GraphGUIHelper corresponding to a specific graph class
 * The factory itself is managed as a singleton.
 * The instances of GraphGUIHelper are managed through a map establishing the correspondence 
 * 	between graph class and GraphGUIHelper instance. Theses instances are generated using 
 *  the Service Provider interface implying each GraphGUIHelper extension have to declare 
 *  the annotation "@ProviderFor(GraphGUIHelper)"
 * 
 * A user may access statically to the factory singleton through the getFactory method and then call the
 * 	getGraphGUIHelper method to obtain the helper instance
 * 
 * 
 * @author Lionel Spinelli
 *
 * @param <V>
 * @param <E>
 */

public class GraphGUIHelperFactory {
	
	// The factory singleton
	private static GraphGUIHelperFactory factory = null;
	
	// The map establishing the correspondence between graph class and GraphGUIHelper instance
	private HashMap<String, GraphGUIHelper<?,?,?>> guiGraphHelpers = new HashMap<String, GraphGUIHelper<?,?,?>>();  
	
	
	/**
	 * Factory trivial creator
	 * 
	 */
	private GraphGUIHelperFactory(){
		
        Iterator<GraphGUIHelper> helpers = ServiceLoader.load( GraphGUIHelper.class).iterator();
        while (helpers.hasNext()) {
            try {
            	GraphGUIHelper<?,?,?> helper = helpers.next();
            	String graph_class_name = helper.getGraphClassName();
                    if( graph_class_name != null && !graph_class_name.isEmpty()) {
                    	guiGraphHelpers.put( graph_class_name, helper);
                    }
            }
            catch (ServiceConfigurationError e){
                    // For now just ignore the exceptions
            }
        }
	}
	
	
	/**
	 * Method providing access to the factory instance
	 * 
	 * @return the GraphGUIHelperFactory singleton 
	 */
	public static GraphGUIHelperFactory getFactory(){
		
		if( factory == null){
			factory = new GraphGUIHelperFactory();
		}
		
		return factory;
	}
	
	
	/**
	 * Give access to the GraphGUIHelper corresponding to the given graph class
	 * 
	 * @param graph_class the name of the graph class name for which the GUI helper is required
	 * @return the instance of GraphGUIHelper corresponding to the graph class
	 */
	public GraphGUIHelper<?,?,?> getGraphGUIHelper( String graph_class_name) throws ClassNotFoundException, IllegalAccessException, InstantiationException{
		
		GraphGUIHelper<?,?,?> helper = null;
		
		if( guiGraphHelpers.containsKey( graph_class_name)){
			helper = guiGraphHelpers.get( graph_class_name);
		}
		else{
			throw new ClassNotFoundException( "GraphGUIhelperFactory.getGraphGUIHelper : No GraphGUIHelper found for graph name : " + graph_class_name);
		}
		
		return helper;
	}
	
	
	/**
	 * Give access to the GraphGUIHelper corresponding to the given graph
	 * 
	 * @param graph the instance of the graph for which the GUI helper is required
	 * @return the instance of GraphGUIHelper corresponding to the graph class
	 */
	public GraphGUIHelper<?,?,?> getGraphGUIHelper( Graph<?,?> graph) throws ClassNotFoundException, IllegalAccessException, InstantiationException{
		
		if( graph != null){
			return getGraphGUIHelper( graph.getClass().getName());
		}
		else{
			throw new ClassNotFoundException( "GraphGUIhelperFactory.getGraphGUIHelper : the provided graph is null");
		}
	}
	
	
}
