package org.ginsim.graph;

import java.util.Collection;

abstract public class AbstractGraphFrontend<V, E extends Edge<V>> implements Graph<V, E> {

	private final GraphBackend<V,E> backend;

	/**
	 * Create a new graph with the default backend.
	 */
	public AbstractGraphFrontend() {
		this(new JgraphtBackendImpl<V, E>());
	}

	/**
	 * Create a new graph with a backend of choice.
	 * @param backend
	 */
	public AbstractGraphFrontend(GraphBackend<V, E> backend) {
		this.backend = backend;
	}

	/**
	 * Create a vertex object (but do not add it to the graph).
	 * 
	 * @param mode
	 * @return
	 */
	abstract protected V createVertex(int mode);
	
	/**
	 * Create an edge object (but do not add it to the graph).
	 * 
	 * @param source
	 * @param target
	 * @param mode
	 * @return
	 */
	abstract protected E createEdge(V source, V target, int mode);

	
	/**
	 * Hack required to forward the backend to the GUI...
	 * @return
	 */
	public GraphBackend<V, E> getBackend() {
		return backend;
	}
	
	@Override
	public V addVertex(int mode) {
		V vertex = createVertex(mode);
		backend.addVertexInBackend(vertex);
		return vertex;
	}

	@Override
	public E addEdge(V source, V target, int mode) {
		E edge = createEdge(source, target, mode);
		backend.addEdgeInBackend(edge);
		return edge;
	}

	@Override
	public boolean removeVertex(V vertex) {
		return backend.removeVertex(vertex);
	}

	@Override
	public boolean removeEdge(E edge) {
		return backend.removeEdge(edge);
	}

	@Override
	public GraphView getGraphView() {
		return backend.getGraphView();
	}
	
	@Override
	public int getVertexCount() {
		return backend.getVertexCount();
	}

	@Override
	public E getEdge(V source, V target) {
		return backend.getEdge(source, target);
	}

	@Override
	public Collection<E> getEdges() {
		return backend.getEdges();
	}

	@Override
	public Collection<V> getVertices() {
		return backend.getVertices();
	}

	@Override
	public Collection<E> getIncomingEdges(V vertex) {
		return backend.getIncomingEdges(vertex);
	}

	@Override
	public Collection<E> getOutgoingEdges(V vertex) {
		return backend.getOutgoingEdges(vertex);
	}
}
