package org.ginsim.core.graph;

import java.awt.Dimension;
import java.util.Collection;
import java.util.Map;

import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.graph.view.ViewCopyHelper;
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
	 * Transfer view settings from a related graph.
	 * The position and styles of matching nodes and edges
	 * will be copied from the source graph to the current one
	 * 
	 * @param src the source graph
	 */
	void copyView(Graph<V,E> src, ViewCopyHelper<Graph<V,E>,V,E> helper);
	
	/**
	 * The graph has changed, all listeners will be notified.
	 * It will also be marked as unsaved.
	 * @param type
     * @param data
	 */
	void fireGraphChange(GraphChangeType type, Object data);

	/**
	 * Retrieve the name to use when displaying a graph node.
	 *
	 * @param node
	 * @return
	 */
	String getDisplayName(V node);

	/**
	 * Retrieve graph-level attributes
	 *
	 * @return a map of attribute values, it can be empty but should not be null
	 */
	Map<String,String> getAttributes();

	void setAttribute(String name, String value);

}
