package org.ginsim.core.graph.backend;

import org.ginsim.core.graph.common.Edge;
import org.jgrapht.EdgeFactory;

/**
 * Edge factory for jgrapht: this is mainly a workaround for the way edges are (re)created
 * when they were first made with copies of the source or the target vertex.
 */
public class GsJgraphtEdgeFactory<V> implements EdgeFactory<V, Edge<V>> {

	public Edge<V> createEdge(V source, V target) {
		return new Edge<V>(source, target);
	}
}
