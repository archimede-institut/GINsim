package org.ginsim.core.graph.view.style;

import org.ginsim.core.graph.common.Edge;

/**
 * Define a style provider: it provides styles for nodes and edges
 * and can be applied to a StyleManager to temporarily override the graph styles.
 * 
 * @author Aurelien Naldi
 *
 * @param <V>
 * @param <E>
 */
public interface StyleProvider<V, E extends Edge<V>> {

	/**
	 * Get the style to use for a node.
	 * 
	 * @param node
	 * @return the style to use or null to use the graph's style
	 */
	NodeStyle<V> getNodeStyle(V node);
	
	/**
	 * Get the style used for an edge.
	 * @param edge
	 * @return the style to use, or null to use the graph's style
	 */
	EdgeStyle<V, E> getEdgeStyle(E edge);
}
