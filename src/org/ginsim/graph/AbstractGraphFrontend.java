package org.ginsim.graph;

import java.util.Collection;
import java.util.List;

import org.ginsim.graph.backend.GraphBackend;
import org.ginsim.graph.backend.GraphViewBackend;
import org.ginsim.graph.backend.JgraphtBackendImpl;

import fr.univmrs.tagc.GINsim.graph.GsEdgeAttributesReader;
import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;

abstract public class AbstractGraphFrontend<V, E extends Edge<V>> implements Graph<V, E>, GraphView {

	private final GraphBackend<V,E> graphBackend;
	private final GraphViewBackend viewBackend;

	
	/**
	 * Create a new graph with the default back-end.
	 */
	public AbstractGraphFrontend() {
		this( new JgraphtBackendImpl<V, E>());
		
	}

	
	/**
	 * Create a new graph with a back-end of choice.
	 * @param backend
	 */
	public AbstractGraphFrontend(GraphBackend<V, E> backend) {
		this.graphBackend = backend;
		viewBackend = graphBackend.getGraphViewBackend();
	}

	/**
	 * Add a vertex to this graph structure
	 * 
	 * @param vertex
	 * @return
	 */
	public boolean addVertexInBackend(V vertex) {
		return graphBackend.addVertexInBackend(vertex);
	}
	
	/**
	 * Add an edge to this graph structure.
	 * 
	 * @param edge
	 * @return
	 */
	public boolean addEdgeInBackend(E edge) {
		return graphBackend.addEdgeInBackend(edge);
	}
	
    /**
     * Remove a vertex from the graph.
     * 
     * @param vertex
     * @return true if the vertex was effectively removed
     */ 
	public boolean removeVertex(V vertex) {
		return graphBackend.removeVertex(vertex);
	}

	
    /**
     * Remove an edge from the graph.
     * 
     * @param edge
     * @return true if the edge was effectively removed
     */
	public boolean removeEdge(E edge) {
		return graphBackend.removeEdge(edge);
	}

	
	/**
	 * Hack required to forward the back-end to the GUI...
	 * @return
	 */
	public GraphBackend<V, E> getBackend() {
		return graphBackend;
	}

	
	/**
	 * @return the number of vertex in this graph.
	 */
	@Override
	public int getVertexCount() {
		return graphBackend.getVertexCount();
	}

	
    /**
     * @param source
     * @param target
     * @return the edge between source and target or null if not found.
     */
	@Override
	public E getEdge(V source, V target) {
		return graphBackend.getEdge(source, target);
	}

	
    /**
     * @return a Collection of the graph edges.
     */
	@Override
	public Collection<E> getEdges() {
		return graphBackend.getEdges();
	}

	
    /**
     * @return a Collection of the graph vertices.
     */
	@Override
	public Collection<V> getVertices() {
		return graphBackend.getVertices();
	}
	
	
    /**
     * @param vertex
     * @return true if the vertex is in the graph, false if not.
     */
	@Override
    public boolean containsVertex(V vertex) {
        return graphBackend.containsVertex(vertex);
    }
    
	
    /**
     * @param from
     * @param to
     * @return true if an edge between the two provided vertices exists in the graph, false if not.
     */
	@Override
    public boolean containsEdge(V from, V to) {
        return graphBackend.containsEdge(from, to);
    }	
	

    /**
     * @param vertex
     * @return a Collection of the incoming edges of the given vertex.
     */
	@Override
	public Collection<E> getIncomingEdges(V vertex) {
		return graphBackend.getIncomingEdges(vertex);
	}
	
    
    /**
     * @param vertex
     * @return a Collection of the outgoing edges of the given vertex.
     */
	@Override
	public Collection<E> getOutgoingEdges(V vertex) {
		return graphBackend.getOutgoingEdges(vertex);
	}
	
	@Override
	public GsEdgeAttributesReader getEdgeReader() {
		return viewBackend.getEdgeReader();
	}
	
	@Override
	public GsVertexAttributesReader getVertexReader() {
		return viewBackend.getVertexReader();
	}
	
	@Override
	public List merge( Graph<V, E> graph) {
		
		this.doMerge( graph);
		List v = this.doMerge(graph);
        if (v != null) {
        	//FIXME Change firegraphChange call
        	//fireGraphChange( CHANGE_MERGED, v);
        	//TODO Move the select on the GUI side
        	//graphManager.select(v);
        }
    	return v;
	}
	
	abstract protected List<?> doMerge( Graph<V, E> graph);

	@Override
	public abstract Graph<V, E> getSubgraph(Collection<V> vertex, Collection<E> edges);

}
