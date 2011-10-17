package org.ginsim.graph;

public interface GraphBackend<V, E extends Edge<V>> extends Graph<V,E> {

	/**
	 * Add a vertex directly.
	 * The frontend graph is responsible for creating the vertex object and calling this method in the backend.
	 * 
	 * @param vertex
	 * @return
	 */
	public boolean addVertexInBackend (V vertex);
	
	/**
	 * Add an edge directly.
	 * The frontend graph is responsible for creating the edge and calling this method in the backend.
	 * 
	 * @param edge
	 * @return
	 */
	public boolean addEdgeInBackend (E edge);
}
