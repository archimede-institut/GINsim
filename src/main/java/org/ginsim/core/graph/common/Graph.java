package org.ginsim.core.graph.common;

import java.awt.Dimension;
import java.util.Collection;

import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.backend.GraphViewListener;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.NodeAttributesReader;


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
	public NodeAttributesReader getNodeAttributeReader();
	
	/**
	 * Get the global bounds of the graph.
	 * 
	 * @return
	 */
	public Dimension getDimension();
	
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


	/**
	 * Add a listener for view change events.
	 * Note: only one listener is supported: the GraphGUI
	 * @param listener
	 */
	void addViewListener(GraphViewListener listener);
	
	/**
	 * the graph has changed, all listeners will be notified.
	 * it will also be marked as unsaved.
	 * @param type
     * @param data
	 */
	void fireGraphChange(GraphChangeType type, Object data);

}
