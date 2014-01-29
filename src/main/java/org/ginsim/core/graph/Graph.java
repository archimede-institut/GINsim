package org.ginsim.core.graph;

import java.awt.Dimension;
import java.util.Collection;

import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.graph.view.style.StyleManager;


/**
 * The main graph object in GINsim.
 * Beside the basic graph methods from the GraphModel interface, this provides
 * access to the view (styles and attribute readers), and define methods to save a graph.
 *
 * @author Aurelien Naldi
 * @author Lionel Spinelli
**/
public interface Graph<V,E extends Edge<V>> extends GraphModel<V,E>{

	/**
	 * Retrieve the style manager for this graph.
	 * @return the style manager
	 */
	StyleManager<V, E> getStyleManager();
	
	/**
	 * Give access to the attribute reader of edges
	 * 
	 * @return the attribute reader of edges
	 */
	EdgeAttributesReader getEdgeAttributeReader();
	
	/**
	 * Give access to the attribute reader of vertices
	 * 
	 * @return the attribute reader of vertices
	 */
	NodeAttributesReader getNodeAttributeReader();
	
	/**
	 * Get the global bounds of the graph.
	 * 
	 * @return
	 */
	Dimension getDimension();
	
	/**
	 * Save this graph.
     *
	 * @param path
	 */
	void save(String path) throws GsException;
	
	/**
	 * Save some components of this graph.
     *
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
	 * The graph has changed, all listeners will be notified.
	 * It will also be marked as unsaved.
	 * @param type
     * @param data
	 */
	void fireGraphChange(GraphChangeType type, Object data);

}
