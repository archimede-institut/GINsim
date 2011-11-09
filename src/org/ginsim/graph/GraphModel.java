package org.ginsim.graph;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

import org.ginsim.exception.GsException;

import fr.univmrs.tagc.GINsim.annotation.Annotation;
import fr.univmrs.tagc.GINsim.graph.GsGraphAssociatedObjectManager;
import fr.univmrs.tagc.GINsim.graph.GsGraphListener;

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
	
    
    
	
    //----------------------   GRAPH VERTICES AND EDGES MANAGEMENT METHODS -------------------------------

	/**
	 * @return the number of vertex in this graph.
	 */
	public int getVertexCount();
	
    
    /**
     * @return a Collection of the graph vertices.
     */
    public Collection<V> getVertices();
    
    
	/**
	 * Give access to the vertex named with the given name
	 * 
	 * @param id name of a vertex
	 * @return the vertex corresponding to this unique id or null if not found.
	 */
	public V getVertexByName( String id);
	
    /**
     * Return the size of the node order
     * 
     * @return the size of the node order
     */
	public int getNodeOrderSize();
	
	
	/**
	 * Search the vertices with ID matching the given regular expression. 
	 * Other kind of graph could overwrite this method. 
	 * 
	 * @param regexp the regular expression vertex ID must match to be selected
	 * @return a Vector of vertices
	 */
	public Vector<V> searchVertices( String regexp);
	
	
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
	 * Find the shortest path between the two given vertices
	 * 
	 * @param source the vertex at the beginning of the searched path
	 * @param target the vertex at the end of the searched path
	 * @return the list of edges composing the shortest path
	 */
	List<E> getShortestPath(V source, V target);
	
    
    /**
     * Give access to the name of the graph
     * 
     * @return the name associated with this graph.
     */
    public String getGraphName();
   
    
    /**
     * Allow to set the name of the graph
     * 
     * @param graph_name the name associated with this graph.
     */
    public void setGraphName( String graph_name) throws GsException ;
    
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
    
    
    //----------------------   ASSOCIATED OBJECTS METHODS --------------------------------------------
    
    
    /**
     * Give access to the list of registered object managers
     * 
     * @return the list of registered object managers
     */
    public List<GsGraphAssociatedObjectManager> getObjectManagerList();
    
    
    /**
     * @return a vector of action related to this kind of graph.
     */
    public List getSpecificObjectManager();
    
    /**
     * Allow to associate objects with a graph to retrieve them later.
     *
     * @see #getObject(Object)
     * @see #removeObject(Object)
     * @param key
     * @param obj
     */
    public void addObject(Object key, Object obj);
    
    
    /**
     * Allow to associate objects with a graph to retrieve them later.
     *
     * @see #addObject(Object, Object)
     * @param key
     * @param create if true, a non-defined object will be created
     * @return the associated object
     */
    public Object getObject( Object key, boolean create);
    
    
	
    //----------------------   EVENT MANAGEMENT METHODS --------------------------------------------
	
	/**
	 * Register a listener on this graph
	 * 
	 * @param g_listener the graph listener
	 */
    public void addGraphListener(GsGraphListener<V,E> g_listener);
    
    
	/**
	 * Remove a graph listener from this graph
	 * 
	 * @param g_listener the graph listener to remove
	 */
    public void removeGraphListener(GsGraphListener<V,E> g_listener);
    
	
    
    //----------------------   ANNOTATION METHODS --------------------------------------------

    
	/**
     * Give access to the annotation associated with this graph.
     * 
	 * @return the association associated with this graph
	 */
	public Annotation getAnnotation();

	

	
}
