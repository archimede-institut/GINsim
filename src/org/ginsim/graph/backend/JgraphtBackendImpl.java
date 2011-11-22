package org.ginsim.graph.backend;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.ginsim.graph.common.AbstractGraph;
import org.ginsim.graph.common.Edge;
import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.alg.StrongConnectivityInspector;
import org.jgrapht.graph.ListenableDirectedGraph;

public class JgraphtBackendImpl<V, E extends Edge<V>> extends ListenableDirectedGraph<V, E> implements GraphBackend<V, E> {
	private static final long serialVersionUID = -7766943723639796018L;
	
	private AbstractGraph<V,E> frontend = null;
	private final GraphViewBackend graphViewBackend;

	public JgraphtBackendImpl() {
		// FIXME: remove the edgeFactory (with better integration with the underlying graph)
		super(new GsJGraphtBaseGraph<V, E>(new GsJgraphtEdgeFactory()));
		graphViewBackend = new GraphViewBackendImpl();
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
	public boolean addNodeInBackend(V vertex) {
		return super.addVertex(vertex);
	}
	
	/**
	 * remove a node from the JgraphT graph
	 * 
	 * @param node the node to remove
	 * @return true if the node was correctly removed, false if not
	 */
	@Override
	public boolean removeNode(V node) {
		
		return super.removeVertex( node);
	}
	
	@Override
	public int getNodeCount() {
		return vertexSet().size();
	}

	@Override
	public Collection<E> getEdges() {
		return edgeSet();
	}

	@Override
	public Collection<V> getNodes() {
		return vertexSet();
	}
	
	/**
	 * Give access to the vertex named with the given name
	 * 
	 * @param id name of a vertex
	 * @return the vertex corresponding to this unique id or null if not found.
	 */
	public V getNodeByName( String id) {
		Iterator<V> it = getNodes().iterator();
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
    public boolean containsNode(V vertex) {
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
		return graphViewBackend;
	}

	public void addViewListener(GraphViewListener listener) {
		getGraphViewBackend().addViewListener(listener);
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
