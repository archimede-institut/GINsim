package org.ginsim.graph.backend;

import java.util.Collection;

import org.ginsim.graph.AbstractGraphFrontend;
import org.ginsim.graph.Edge;
import org.jgrapht.graph.ListenableDirectedGraph;

import fr.univmrs.tagc.GINsim.jgraph.GsJGraphtBaseGraph;
import fr.univmrs.tagc.GINsim.jgraph.GsJgraphtEdgeFactory;

public class JgraphtBackendImpl<V, E extends Edge<V>> extends ListenableDirectedGraph<V, E> implements GraphBackend<V, E> {
	private static final long serialVersionUID = -7766943723639796018L;
	
	private AbstractGraphFrontend<V,E> frontend = null;
	private GraphViewBackend graphViewBackend;
	
	public JgraphtBackendImpl() {
		// FIXME: remove the edgeFactory (with better integration with the underlying graph)
		super(new GsJGraphtBaseGraph<V, E>(new GsJgraphtEdgeFactory()));
	}
	
	/**
	 * Add an edge in the JGraphT Graph
	 * 
	 * @param edge
	 * @return true if the edge was correctly added, false if not.
	 */
	@Override
	public boolean addEdgeInBackend(E edge) {
		return super.addEdge(edge.getSource(), edge.getTarget(), edge);
	}
	
    /**
     * Add a vertex in the JGraphT graph
     * 
     * @param vertex
     * @return true if the vertex was correctly added, false if not.
     */
	@Override
	public boolean addVertexInBackend(V vertex) {
		return super.addVertex(vertex);
	}
	
	@Override
	public int getVertexCount() {
		return vertexSet().size();
	}

	@Override
	public Collection<E> getEdges() {
		return edgeSet();
	}

	@Override
	public Collection<V> getVertices() {
		return vertexSet();
	}

	@Override
	public Collection<E> getIncomingEdges(V vertex) {
		return incomingEdgesOf(vertex);
	}

	@Override
	public Collection<E> getOutgoingEdges(V vertex) {
		return outgoingEdgesOf(vertex);
	}
	
    /**
     * @param vertex
     * @return true if the vertex is in the graph, false if not.
     */
	@Override
    public boolean containsVertex(V vertex) {
        return super.containsVertex(vertex);
    }
    
    /**
     * @param from
     * @param to
     * @return true if an edge between the two provided vertices exists in the graph, false if not.
     */
	@Override
    public boolean containsEdge(V from, V to) {
        return super.containsEdge(from, to);
    }

	// TODO is this method useful in back-end? Creation of new object have to pass directly from front-end
//	@Override
//	public E addEdge(V source, V target, int mode) {
//		if (frontend == null) {
//			throw new RuntimeException("No frontend is available to create the new edge");
//		}
//		E edge = frontend.createEdge(source, target, mode);
//		addEdgeInBackend(edge);
//		return edge;
//	}

	// TODO is this method useful in back-end? Creation of new object have to pass directly from front-end
//	@Override
//	public V addVertex(int mode) {
//		if (frontend == null) {
//			throw new RuntimeException("No frontend is available to create the new edge");
//		}
//		V vertex = frontend.createVertex(mode);
//		addVertexInBackend(vertex);
//		return vertex;
//	}

	@Override
	public GraphViewBackend getGraphViewBackend() {
		if (graphViewBackend == null) {
			graphViewBackend = new JgraphtViewBackendImpl(this);
		}
		return graphViewBackend;
	}

	/**
	 * Change the back-end used for graph view.
	 * Used to switch from the generic back-end to a jgraph one.
	 * @param backend
	 */
	public void setGraphViewBackend(GraphViewBackend backend) {
		if (graphViewBackend != null) {
			// FIXME: transfer view info from one to the other
		}
		this.graphViewBackend = backend;
	}
}
