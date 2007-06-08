package fr.univmrs.ibdm.GINsim.jgraph;

import org._3pq.jgrapht.Edge;
import org._3pq.jgrapht.edge.EdgeFactories;

/**
 * Edge factory for jgrapht: this is mainly a workaround for the way edges are (re)created
 * when they were first made with copies of the source or the target vertex.
 */
public class GsJgraphtEdgeFactory extends EdgeFactories.DirectedEdgeFactory {

	private static final long serialVersionUID = 6547654786749696432L;

	public Edge createEdge(Object source, Object target) {
		return new GsJgraphDirectedEdge(source, target, null);
	}
}
