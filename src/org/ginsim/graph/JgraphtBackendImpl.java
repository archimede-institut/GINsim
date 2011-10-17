package org.ginsim.graph;

import java.util.Collection;

import org.jgrapht.graph.ListenableDirectedGraph;

import fr.univmrs.tagc.GINsim.jgraph.GsJGraphtBaseGraph;
import fr.univmrs.tagc.GINsim.jgraph.GsJgraphtEdgeFactory;

public class JgraphtBackendImpl<V, E extends Edge<V>> extends ListenableDirectedGraph<V, E> implements GraphBackend<V, E> {
	private static final long serialVersionUID = -7766943723639796018L;
	
	
	private AbstractGraphFrontend<V,E> frontend = null;
	private GraphView graphView;
	
	public JgraphtBackendImpl() {
		// FIXME: remove the edgeFactory (with better integration with the underlying graph)
		super(new GsJGraphtBaseGraph<V, E>(new GsJgraphtEdgeFactory()));
	}

	@Override
	public boolean addEdgeInBackend(E edge) {
		return super.addEdge(edge.getSource(), edge.getTarget(), edge);
	}
	
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

	@Override
	public E addEdge(V source, V target, int mode) {
		if (frontend == null) {
			throw new RuntimeException("No frontend is available to create the new edge");
		}
		E edge = frontend.createEdge(source, target, mode);
		addEdgeInBackend(edge);
		return edge;
	}

	@Override
	public V addVertex(int mode) {
		if (frontend == null) {
			throw new RuntimeException("No frontend is available to create the new edge");
		}
		V vertex = frontend.createVertex(mode);
		addVertexInBackend(vertex);
		return vertex;
	}

	@Override
	public GraphView getGraphView() {
		if (graphView == null) {
			graphView = new GraphViewImpl();
		}
		return graphView;
	}
}
