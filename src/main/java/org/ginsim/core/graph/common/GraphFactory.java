package org.ginsim.core.graph.common;

import org.ginsim.core.graph.view.style.EdgeStyle;
import org.ginsim.core.graph.view.style.NodeStyle;

/**
 * Factory to open or create specialised gryph instances.
 * 
 * @author Aurelien Naldi
 *
 * @param <G>
 */
public interface GraphFactory<G extends Graph<?,?>> {
	
    /**
     * Return the class of graph this factory is managing
     * 
     * @return the name of the class of graph this factory is managing
     */
	Class<G> getGraphClass();
	
    /**
     * Return the type of graph this factory is managing
     * 
     * @return the name of the type of graph this factory is managing
     */
	String getGraphType();
	
	/**
	 * Return the class of the parser to use to read from file the type
	 * of graph the factory manager
	 * 
	 * @return the class of the parser to use with this factory
	 */
	Class getParser();
	
    /**
     * Create a new graph of the type factory is managing
     * 
     * @return an instance of the graph type the factory is managing
     */
	G create();

	NodeStyle createDefaultNodeStyle(G graph);
	
	EdgeStyle createDefaultEdgeStyle(G graph);
}
