package org.ginsim.graph.backend;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.ginsim.graph.common.AbstractGraphFrontend;
import org.ginsim.graph.common.Edge;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.jgrapht.graph.ListenableDirectedGraph;

public class JgraphtBackendImpl<V, E extends Edge<V>> extends ListenableDirectedGraph<V, E> implements GraphBackend<V, E> {
	private static final long serialVersionUID = -7766943723639796018L;
	
	private AbstractGraphFrontend<V,E> frontend = null;
	private GraphViewBackend graphViewBackend;
	
	public JgraphtBackendImpl() {
		// FIXME: remove the edgeFactory (with better integration with the underlying graph)
		super(new GsJGraphtBaseGraph<V, E>(new GsJgraphtEdgeFactory()));
	}
	
	/**
	 * Add an edge in the JGraphT Graph
	 * 
	 * @param edge
	 * @return true if the edge was correctly added, false if not.
	 */
	@Override
	public boolean addEdgeInBackend(E edge) {
		return super.addEdge(edge.getSource(), edge.getTarget(), edge);
	}
	
    /**
     * Add a vertex in the JGraphT graph
     * 
     * @param vertex
     * @return true if the vertex was correctly added, false if not.
     */
	@Override
	public boolean addVertexInBackend(V vertex) {
		return super.addVertex(vertex);
	}
	
	@Override
	public int getVertexCount() {
		return vertexSet().size();
	}

	@Override
	public Collection<E> getEdges() {
		return edgeSet();
	}

	@Override
	public Collection<V> getVertices() {
		return vertexSet();
	}
	
	/**
	 * Give access to the vertex named with the given name
	 * 
	 * @param id name of a vertex
	 * @return the vertex corresponding to this unique id or null if not found.
	 */
	public V getVertexByName( String id) {
		Iterator<V> it = getVertices().iterator();
		while (it.hasNext()) {
			V vertex = it.next();
			if (id.equals(vertex.toString())) {
				return vertex;
			}
		}
		return null;
	}

	@Override
	public Collection<E> getIncomingEdges(V vertex) {
		return incomingEdgesOf(vertex);
	}

	@Override
	public Collection<E> getOutgoingEdges(V vertex) {
		return outgoingEdgesOf(vertex);
	}
	
    /**
     * @param vertex
     * @return true if the vertex is in the graph, false if not.
     */
	@Override
    public boolean containsVertex(V vertex) {
        return super.containsVertex(vertex);
    }
    
    /**
     * @param from
     * @param to
     * @return true if an edge between the two provided vertices exists in the graph, false if not.
     */
	@Override
    public boolean containsEdge(V from, V to) {
        return super.containsEdge(from, to);
    }
	
	/**
	 * Find the shortest path between the two given vertices
	 * 
	 * @param source the vertex at the beginning of the searched path
	 * @param target the vertex at the end of the searched path
	 * @return the list of edges composing the shortest path
	 */
	@Override
    public List<E> getShortestPath(V source, V target) {
		
        return DijkstraShortestPath.findPathBetween( this, source, target);
    }

	@Override
	public GraphViewBackend getGraphViewBackend() {
		if (graphViewBackend == null) {
			graphViewBackend = new JgraphtViewBackendImpl(this);
		}
		return graphViewBackend;
	}

	/**
	 * Change the back-end used for graph view.
	 * Used to switch from the generic back-end to a jgraph one.
	 * @param backend
	 */
	public void setGraphViewBackend(GraphViewBackend backend) {
		if (graphViewBackend != null) {
			if (graphViewBackend instanceof JgraphtViewBackendImpl) {
				((JgraphtViewBackendImpl)graphViewBackend).setGraphViewBackend(backend);
			}
			// FIXME: transfer view info from one to the other
		}
		this.graphViewBackend = backend;
	}

	
	/**
	 * Return a list of set of vertex, each set containing a strongly connected component of the graph
	 * 
	 * @return a list of set of vertex, each set containing a strongly connected component of the graph
	 */
	@Override
	public List<Set<V>> getStronglyConnectedComponents() {
		
		return new StrongConnectivityInspector<V, E>( this).stronglyConnectedSets();
	}
}
