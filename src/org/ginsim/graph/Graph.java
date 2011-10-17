package org.ginsim.graph;

import java.util.Collection;

/**
 * Interface for the main objects: graphs.
 * 
 * This should provide only the main methods to edit and browse a graph.
 * As the existing stuff did a lot of things, I'll start by putting notes about what we may want to have as well...
 * 
 * @author Aurelien Naldi
 *
 * @param <V> type for vertices
 * @param <E> type for edges
 */
public interface Graph<V,E extends Edge<V>> {

	/*
	 * so, what do we want to be able to do here?
	 * 
	 * First some simple graph stuff, this should be fairly clear:
	 *  * add and remove vertices and edges
	 *  * get the list of existing vertices and edges
	 *  * access the GraphView object
	 * 
	 * We used to have some extra stuff:
	 *   * store some extra information: name, annotation
	 *   * provide save methods. remember save path
	 *   * metadata: can this graph be edited interactively, how ?
	 *   * GUI consistency helpers: block edition/close
	 *   * service providers: layout, actions, exports. Should be done outside of the graph
	 *   * copy/paste a subgraph
	 */

	/**
	 * Grab the GraphView associated to this graph.
	 * It provide access to all visual information: positions, sizes, colors...
	 * 
	 * @return the view of this graph.
	 */
	public GraphView getGraphView();
	
	/**
	 * Add an edge between two vertices.
	 * 
	 * @param source source vertex for this edge
	 * @param target target vertex for this edge
	 * 
	 * @return the new vertex
	 */
	public E addEdge (V source, V target, int mode);
	
	/**
	 * Add a new vertex.
	 * 
	 * @param mode
	 * @return the new vertex
	 */
	public V addVertex (int mode);
	
	
    /**
     * remove a vertex from the graph.
     * @param vertex
     */
    public boolean removeVertex(V vertex);
    /**
     * remove an edge from the graph.
     * @param edge
     */
    public boolean removeEdge(E edge);

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


	public Collection<E> getEdges();
	public Collection<V> getVertices();

    
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
}
