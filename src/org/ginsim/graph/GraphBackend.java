package org.ginsim.graph;

import java.util.Collection;

public interface GraphBackend<V, E extends Edge<V>> {

	/**
	 * Add a vertex to the graph.
	 * The front-end graph is responsible for creating the vertex object and calling this method in the backend.
	 * 
	 * @param vertex
	 * @return
	 */
	public boolean addVertexInBackend (V vertex);
	
	
	/**
	 * Add an edge  to the graph.
	 * The front-end graph is responsible for creating the edge and calling this method in the backend.
	 * 
	 * @param edge
	 * @return
	 */
	public boolean addEdgeInBackend (E edge);
	
	
    /**
     * @param vertex
     * @return true if the vertex is in the graph, false if not.
     */
    public boolean containsVertex(V vertex);
    
	
    /**
     * 
     * @param from
     * @param to
     * @return true if an edge between the two provided vertices exists in the graph, false if not.
     */
    public boolean containsEdge(V from, V to);
	
	
    /**
     * remove a vertex from the graph.
     * @param vertex
     * @return true if the vertex was correctly removed, false if not.
     */
	public boolean removeVertex(V vertex);

	
    /**
     * remove an edge from the graph.
     * @param edge
     * @return true if the edge was correctly removed, false if not.
     */
	public boolean removeEdge(E edge);
	
	
    /**
     * @return a Collection of the graph vertices.
     */
    public Collection<V> getVertices();
	
    
	/**
	 * @return the number of vertex in this graph.
	 */
	public int getVertexCount();
	
    
    /**
     * @param source
     * @param target
     * @return the edge between source and target or null if not found.
     */
    public E getEdge(V source, V target);
    
    
    /**
     * @return a Collection of the graph edges.
     */
	public Collection<E> getEdges();
	
	
    /**
     * @param vertex
     * @return incoming edges of the given vertex.
     */
    public Collection<E> getIncomingEdges(V vertex);
    
    
    /**
     * @param vertex
     * @return outgoing edges of the given vertex.
     */
    public Collection<E> getOutgoingEdges(V vertex);
    
    
	/**
	 * Grab the GraphView associated to this graph.
	 * It provide access to all visual information: positions, sizes, colors...
	 * 
	 * @return the view of this graph.
	 */
	public GraphView getGraphView();
}
