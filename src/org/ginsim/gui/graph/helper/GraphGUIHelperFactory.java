package org.ginsim.gui.graph.helper;

import java.util.HashMap;

import org.ginsim.graph.Edge;
import org.ginsim.graph.Graph;

/**
 * This factory provides access to the GraphGUIHelper corresponding to a specific graph class
 * The factory itself is managed as a singleton.
 * The instances of GraphGUIHelper are managed through a map establishing the correspondence 
 * 	between graph class and GraphGUIHelper instance
 * 
 * A user may access statically to the factory singleton through the getFactory method and then call the
 * 	getGraphGUIHelper method to obtain the helper instance
 * 
 * Note that all the GraphGUIHelper implementors must be called with the same pattern: '<GraphClassName>GUIHelper.java'
 * 
 * @author Lionel Spinelli
 *
 * @param <V>
 * @param <E>
 */

public class GraphGUIHelperFactory<V, E extends Edge<V>> {
	
	private static final String GRAPH_GUI_HELPER_EXTENSION = "GUIHelper";
	
	// The factory singleton
	private static GraphGUIHelperFactory factory = new GraphGUIHelperFactory();
	
	// The map establishing the correspondence between graph class and GraphGUIHelper instance
	private HashMap<String, GraphGUIHelper<V,E>> guiGraphHelpers = new HashMap<String, GraphGUIHelper<V,E>>();  
	
	/**
	 * Factory trivial creator
	 * 
	 */
	private GraphGUIHelperFactory(){}
	
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
	@SuppressWarnings("unchecked")
	public GraphGUIHelper<V,E> getGraphGUIHelper( String graph_class) throws ClassNotFoundException, IllegalAccessException, InstantiationException{
		
		GraphGUIHelper<V,E> helper = null;
		
		if( guiGraphHelpers.containsKey( graph_class)){
			helper = guiGraphHelpers.get( graph_class);
		}
		else{
			String graph_helper_class = graph_class + GRAPH_GUI_HELPER_EXTENSION;
			helper = (GraphGUIHelper<V,E>) Class.forName( graph_helper_class).newInstance();
			guiGraphHelpers.put( graph_class, helper);
		}
		
		return helper;
	}
	
	/**
	 * Give access to the GraphGUIHelper corresponding to the given graph
	 * 
	 * @param graph the instance of the graph for which the GUI helper is required
	 * @return the instance of GraphGUIHelper corresponding to the graph class
	 */
	public GraphGUIHelper<V,E> getGraphGUIHelper( Graph<V,E> graph) throws ClassNotFoundException, IllegalAccessException, InstantiationException{
		
		if( graph != null){
			return getGraphGUIHelper( graph.getClass().getName());
		}
		else{
			throw new ClassNotFoundException( "GraphGUIhelperFactory.getGraphGUIHelper : the provided graph is null");
		}
	}
	
	
}
