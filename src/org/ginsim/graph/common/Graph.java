package org.ginsim.graph.common;

import java.util.Collection;

import org.ginsim.exception.GsException;


/**
 * This interface layer add the access to "attribute readers" for vertices and edges.
 * When no GUI was here a fallback datastructure was used, otherwise it delegates to JGraph.
 * Copies between the two was used when displaying the graph for example.
 * 
 * These things should be moved here, with a cleaner separation, but how?
 * It should be uniform and stay in sync with the GUI without depending on it, can we do better than what we already had?
 * 
 * @author Aurelien Naldi
 * @author Lionel Spinelli
 *
**/

public interface Graph<V,E extends Edge<V>> extends GraphModel<V,E>{

	

	/**
	 * Give access to the attribute reader of edges
	 * 
	 * @return the attribute reader of edges
	 */
	public EdgeAttributesReader getEdgeAttributeReader();
	
	
	/**
	 * Give access to the attribute reader of vertices
	 * 
	 * @return the attribute reader of vertices
	 */
	public NodeAttributesReader getVertexAttributeReader();
	
	
	/**
	 * Save this graph
	 * TODO: decide if it should be called by the GraphManager or directly
	 * @param path
	 */
	void save(String path) throws GsException;
	
	/**
	 * Save this graph
	 * TODO: decide if it should be called by the GraphManager or directly
	 * @param path
	 */
	void save(String path, Collection<V> nodes, Collection<E> edges) throws GsException;
}
