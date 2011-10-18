package org.ginsim.graph;

import java.util.Collection;

abstract public class AbstractGraphFrontend<V, E extends Edge<V>> implements Graph<V, E> {

	private final GraphBackend<V,E> backend;

	
	/**
	 * Create a new graph with the default back-end.
	 */
	public AbstractGraphFrontend() {
		this(new JgraphtBackendImpl<V, E>());
	}

	
	/**
	 * Create a new graph with a back-end of choice.
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
	 * Hack required to forward the back-end to the GUI...
	 * @return
	 */
	public GraphBackend<V, E> getBackend() {
		return backend;
	}

	
	/**
	 * Add a new vertex.
	 * 
	 * @param mode
	 * @return the new vertex
	 */
	@Override
	public V addVertex(int mode) {
		V vertex = createVertex(mode);
		backend.addVertexInBackend(vertex);
		return vertex;
	}

	
	/**
	 * Add an edge between two vertices.
	 * 
	 * @param source source vertex for this edge
	 * @param target target vertex for this edge
	 * 
	 * @return the new vertex
	 */
	@Override
	public E addEdge(V source, V target, int mode) {
		E edge = createEdge(source, target, mode);
		backend.addEdgeInBackend(edge);
		return edge;
	}

	
    /**
     * remove a vertex from the graph.
     * @param vertex
     */
	@Override
	public boolean removeVertex(V vertex) {
		return backend.removeVertex(vertex);
	}

	
    /**
     * remove an edge from the graph.
     * @param edge
     */
	@Override
	public boolean removeEdge(E edge) {
		return backend.removeEdge(edge);
	}

	
	/**
	 * Grab the GraphView associated to this graph.
	 * It provide access to all visual information: positions, sizes, colors...
	 * 
	 * @return the view of this graph.
	 */	
	@Override
	public GraphView getGraphView() {
		return backend.getGraphView();
	}
	
	
	/**
	 * @return the number of vertex in this graph.
	 */
	@Override
	public int getVertexCount() {
		return backend.getVertexCount();
	}

	
    /**
     * @param source
     * @param target
     * @return the edge between source and target or null if not found.
     */
	@Override
	public E getEdge(V source, V target) {
		return backend.getEdge(source, target);
	}

	
    /**
     * @return a Collection of the graph edges.
     */
	@Override
	public Collection<E> getEdges() {
		return backend.getEdges();
	}

	
    /**
     * @return a Collection of the graph vertices.
     */
	@Override
	public Collection<V> getVertices() {
		return backend.getVertices();
	}
	
	
    /**
     * @param vertex
     * @return true if the vertex is in the graph, false if not.
     */
	@Override
    public boolean containsVertex(V vertex) {
        return backend.containsVertex(vertex);
    }
    
	
    /**
     * @param from
     * @param to
     * @return true if an edge between the two provided vertices exists in the graph, false if not.
     */
	@Override
    public boolean containsEdge(V from, V to) {
        return backend.containsEdge(from, to);
    }	
	

    /**
     * @param vertex
     * @return a Collection of the incoming edges of the given vertex.
     */
	@Override
	public Collection<E> getIncomingEdges(V vertex) {
		return backend.getIncomingEdges(vertex);
	}
	
    
    /**
     * @param vertex
     * @return a Collection of the outgoing edges of the given vertex.
     */
	@Override
	public Collection<E> getOutgoingEdges(V vertex) {
		return backend.getOutgoingEdges(vertex);
	}
}
