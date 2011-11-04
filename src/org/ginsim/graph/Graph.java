package org.ginsim.graph;

import java.util.Collection;
import java.util.List;

/**
 * Interface for the main objects: graphs.
 * 
 * This should provide only the main methods to browse a graph.
 * As not all graphs can be edited, edit methods should be provided by the
 * specialised graph type only.
 * 
 * As the existing stuff did a lot of things, I'll start by putting notes about what we may want to have as well...
 * 
 * @author Aurelien Naldi
 * @author Lionel Spinelli
 *
 * @param <V> type for vertices
 * @param <E> type for edges
 */
public interface Graph<V,E extends Edge<V>> {

	// TODO: this should be done in an enum
    /**  an edged has been added */
    public static final int CHANGE_EDGEADDED = 0;
    /**  an edged has been removed */
    public static final int CHANGE_EDGEREMOVED = 1;
    /** a vertex has been added  */
    public static final int CHANGE_VERTEXADDED = 2;
    /**  a vertex has been removed */
    public static final int CHANGE_VERTEXREMOVED = 3;
    /**  an edge has been modified */
    public static final int CHANGE_EDGEUPDATED = 4;
    /**  a vertex has been modified */
    public static final int CHANGE_VERTEXUPDATED = 5;
    /**  a vertex has been modified */
    public static final int CHANGE_MERGED = 6;
    /**  other kind of change */
    public static final int CHANGE_METADATA = 7;
	
	/*
	 * so, what do we want to be able to do here?
	 * 
	 * First some simple graph stuff, this should be fairly clear:
	 *  * add and remove vertices and edges
	 *  * get the list of existing vertices and edges
	 *  * access the GraphView object
	 * 
	 * We used to have some extra stuff, most of it should go to specialised types:
	 *   * store some extra information: name, annotation
	 *   * provide save methods. remember save path
	 *   * metadata: can this graph be edited interactively, how ?
	 *   * GUI consistency helpers: block edition/close
	 *   * service providers: layout, actions, exports. Should be done outside of the graph
	 *   * copy/paste a subgraph
	 */
	
    
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
     * @return a Collection of the graph vertices.
     */
    public Collection<V> getVertices();
    
    
    /**
     * @return a Collection of the graph edges.
     */
	public Collection<E> getEdges();

	
    /**
     * @param vertex
     * @return true if the vertex is in the graph, false if not.
     */
    public boolean containsVertex(V vertex);
    
    
    /**
     * @param from
     * @param to
     * @return true if an edge between the two provided vertices exists in the graph, false if not.
     */
    public boolean containsEdge(V from, V to);
    
    
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
     * Build a graph from the provided vertices and edges based on the current graph
     * 
     * @param vertex the collection of vertices used to create the subgraph
     * @param edges the collection of edges used to create the subgraph
     * @return a Graph composed of the provided vertices and edges and based on the current graph
     */
    public Graph<V,E> getSubgraph( Collection<V> vertex, Collection<E> edges);
    
    /**
     * Merge the provided graph with the current one
     * 
     * @param graph The graph to merge with the current graph
     */
    public List<?> merge( Graph<V,E> graph);
}
