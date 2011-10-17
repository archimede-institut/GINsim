package org.ginsim.graph;

import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;

/**
 * Base class for all edges: store the source and target nodes.
 * (Just a wrapper around GsDirectedEdge, waiting to be renamed...
 * 
 * @author Aurelien Naldi
 *
 * @param <V> the vertex type
 */
public class Edge<V> extends GsDirectedEdge<V> {

	public Edge(V source, V target) {
		super(source, target);
	}
}
