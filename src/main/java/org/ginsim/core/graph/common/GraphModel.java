package org.ginsim.core.graph.common;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import org.ginsim.common.application.GsException;
import org.ginsim.core.annotation.Annotation;


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

public interface GraphModel<V,E extends Edge<V>> {

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
     * Give access to the name of the graph
     * 
     * @return the name associated with this graph.
     */
    String getGraphName();
    
    
    /**
     * changes (if success) the name associated with this graph.
     * By default only valid xmlid are accepted.
     *
     * @param graphName the new name.
     * @throws GsException if the name is invalid.
     */
    void setGraphName( String graphName) throws GsException;
    
    
    //----------------------   GRAPH SAVING MANAGEMENT METHODS -------------------------------
    
    
    /**
     * Set the mode of saving the graph must used when saved
     * 
     * @param save_mode the mode of saving
     */
    void setSaveMode( int save_mode);
    
    
    /**
     * Return the mode the graph must used when saved
     * 
     * @return the mode the graph must used when saved
     */
    int getSaveMode();
    
	
    //----------------------   GRAPH VERTICES AND EDGES MANAGEMENT METHODS -------------------------------



	/**
	 * Add a node to this graph structure
	 * 
	 * @param node
	 * @return the created node
	 */
	boolean addNode( V node);
	
	/**
	 * Add an edge to this graph structure.
	 * 
	 * @param edge
	 * @return the created edge
	 */
	boolean addEdge( E edge);

    /**
     * Remove a node from the graph.
     * 
     * @param node
     * @return true if the node was effectively removed
     */ 
	boolean removeNode( V node);

    /**
     * Remove an edge from the graph.
     * 
     * @param edge
     * @return true if the edge was effectively removed
     */
	boolean removeEdge( E edge);
	
	/**
	 * Return the number of node in this graph
	 * 
	 * @return the number of node in this graph.
	 */
	int getNodeCount();
	
    
    /**
     * Return the Collection of the graph nodes
     * 
     * @return a Collection of the graph nodes
     */
    Collection<V> getNodes();
    
    
	/**
	 * Give access to the node named with the given name
	 * 
	 * @param id name of a node
	 * @return the node corresponding to this unique id or null if not found.
	 */
	V getNodeByName( String id);
	
    /**
     * Return the size of the node order
     * 
     * @return the size of the node order
     */
	int getNodeOrderSize();
	
	
	/**
	 * Search the vertices with ID matching the given regular expression. 
	 * Other kind of graph could overwrite this method. 
	 * 
	 * @param regexp the regular expression node ID must match to be selected
	 * @return a Vector of vertices
	 */
	List<V> searchNodes( String regexp);
	
	
    /**
     * @param source
     * @param target
     * @return the edge between source and target or null if not found.
     */
    E getEdge(V source, V target);
    
	
    /**
     * @return a Collection of the graph edges.
     */
	Collection<E> getEdges();

	
    /**
     * @param node
     * @return true if the node is in the graph, false if not.
     */
    boolean containsNode(V node);
    
    V getExistingNode(V node);
    
    /**
     * @param from
     * @param to
     * @return true if an edge between the two provided vertices exists in the graph, false if not.
     */
    boolean containsEdge(V from, V to);
    
    
    /**
     * @param node
     * @return incoming edges of the given node.
     */
    Collection<E> getIncomingEdges(V node);
    
    
    /**
     * @param node
     * @return outgoing edges of the given node.
     */
    Collection<E> getOutgoingEdges(V node);
    
    
	/**
	 * Find the shortest path between the two given vertices
	 * 
	 * @param source the node at the beginning of the searched path
	 * @param target the node at the end of the searched path
	 * @return the list of edges composing the shortest path
	 */
	List<E> getShortestPath(V source, V target);
	
	
    
	/**
	 * Return a list of set of node, each set containing a strongly connected component of the graph
	 * 
	 * @return a list of set of node, each set containing a strongly connected component of the graph
	 */
	List<Set<V>> getStronglyConnectedComponents();

    
    /**
     * Build a graph from the provided vertices and edges based on the current graph
     * 
     * @param node the collection of vertices used to create the subgraph
     * @param edges the collection of edges used to create the subgraph
     * @return a Graph composed of the provided vertices and edges and based on the current graph
     */
    Graph<V,E> getSubgraph( Collection<V> node, Collection<E> edges);
    
    /**
     * Merge the provided graph with the current one
     * 
     * @param graph The graph to merge with the current graph
     */
    List<?> merge( Graph<V,E> graph);
    
	
    
    //----------------------   ANNOTATION METHODS --------------------------------------------

    
	/**
     * Give access to the annotation associated with this graph.
     * 
	 * @return the association associated with this graph
	 */
	Annotation getAnnotation();



}
