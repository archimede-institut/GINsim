package org.ginsim.core.graph.view;

import java.awt.Dimension;

import org.ginsim.core.graph.Edge;
import org.ginsim.core.graph.Graph;

/**
 * Provide a mapping between the elements of two graphs,
 * used to transfer visual properties.
 * 
 * @author Aurelien Naldi
 *
 * @param <G> the type of graph
 * @param <V> the type of nodes
 * @param <E> the types of edges
 */
public interface ViewCopyHelper<G extends Graph<V, E>, V,E extends Edge<V>> {

	/**
	 * Retrieve the equivalent node from the source graph.
	 * 
	 * @param node
	 * @return
	 */
	V getSourceNode(V node);

	/**
	 * Retrieve the equivalent edge from the source graph.
	 * 
	 * @param edge
	 * @return
	 */
	E getSourceEdge(E edge);

	/**
	 * Offset to move the last copied element.
	 * 
	 * @return an offset (will be updated by calls to getSourceNode) or null
	 */
	Dimension getOffset();
}
