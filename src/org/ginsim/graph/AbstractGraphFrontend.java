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
		return graphBackend;
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
		graphBackend.addVertexInBackend(vertex);
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
		graphBackend.addEdgeInBackend(edge);
		return edge;
	}

	
    /**
     * Remove a vertex from the graph.
     * 
     * @param vertex
     * @return true if the vertex was effectively removed
     */ 
	@Override
	public boolean removeVertex(V vertex) {
		return graphBackend.removeVertex(vertex);
	}

	
    /**
     * Remove an edge from the graph.
     * 
     * @param edge
     * @return true if the edge was effectively removed
     */
	@Override
	public boolean removeEdge(E edge) {
		return graphBackend.removeEdge(edge);
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
	
	abstract protected List doMerge( Graph<V, E> graph);

	@Override
	public abstract Graph<V, E> getSubgraph(Collection<V> vertex, Collection<E> edges);

}
