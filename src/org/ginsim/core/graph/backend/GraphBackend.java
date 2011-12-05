package org.ginsim.core.graph.backend;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.ginsim.core.graph.common.Edge;

public interface GraphBackend<V, E extends Edge<V>> {

	/**
	 * Add a node to the graph.
	 * The front-end graph is responsible for creating the node object and calling this method in the backend.
	 * 
	 * @param node
	 * @return
	 */
	public boolean addNodeInBackend (V node);
	
	
	/**
	 * Add an edge  to the graph.
	 * The front-end graph is responsible for creating the edge and calling this method in the backend.
	 * 
	 * @param edge
	 * @return
	 */
	public boolean addEdgeInBackend (E edge);
	
	
    /**
     * @param node
     * @return true if the node is in the graph, false if not.
     */
    public boolean containsNode(V node);
    
	
    /**
     * 
     * @param from
     * @param to
     * @return true if an edge between the two provided vertices exists in the graph, false if not.
     */
    public boolean containsEdge(V from, V to);
	
	
    /**
     * remove a node from the graph.
     * @param node
     * @return true if the node was correctly removed, false if not.
     */
	public boolean removeNode(V node);

	
    /**
     * remove an edge from the graph.
     * @param edge
     * @return true if the edge was correctly removed, false if not.
     */
	public boolean removeEdge(E edge);
	
	
    /**
     * @return a Collection of the graph vertices.
     */
    public Collection<V> getNodes();
	
    
	/**
	 * Give access to the node named with the given name
	 * 
	 * @param id name of a node
	 * @return the node corresponding to this unique id or null if not found.
	 */
	public V getNodeByName( String id);
    
	/**
	 * @return the number of node in this graph.
	 */
	public int getNodeCount();
	
    
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
     * @param node
     * @return incoming edges of the given node.
     */
    public Collection<E> getIncomingEdges(V node);
    
    
    /**
     * @param node
     * @return outgoing edges of the given node.
     */
    public Collection<E> getOutgoingEdges(V node);
    
    
	/**
	 * Find the shortest path between the two given vertices
	 * 
	 * @param source the node at the beginning of the searched path
	 * @param target the node at the end of the searched path
	 * @return the list of edges composing the shortest path
	 */
	List<E> getShortestPath( V source, V target);
	
	
	/**
	 * Return a list of set of node, each set containing a strongly connected component of the graph
	 * 
	 * @return a list of set of node, each set containing a strongly connected component of the graph
	 */
	List<Set<V>> getStronglyConnectedComponents();

}
