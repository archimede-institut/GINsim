package fr.univmrs.tagc.GINsim.jgraph;

import org.jgrapht.EdgeFactory;

import fr.univmrs.tagc.GINsim.data.GsDirectedEdge;

/**
 * Edge factory for jgrapht: this is mainly a workaround for the way edges are (re)created
 * when they were first made with copies of the source or the target vertex.
 */
public class GsJgraphtEdgeFactory<V> implements EdgeFactory<V, GsDirectedEdge<V>> {

	public GsDirectedEdge<V> createEdge(V source, V target) {
		return new GsDirectedEdge<V>(source, target);
	}
}
