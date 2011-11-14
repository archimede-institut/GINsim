package org.ginsim.graph.testGraph;

import org.ginsim.graph.common.Graph;

public interface TestGraph extends Graph<TestVertex, TestEdge> {

	/**
	 * Add an edge between two vertices.
	 * 
	 * @param source source vertex for this edge
	 * @param target target vertex for this edge
	 * 
	 * @return the new vertex
	 */
	public TestEdge addEdge (TestVertex source, TestVertex target);
	
	
	/**
	 * Add a new vertex.
	 * 
	 * @return the new vertex
	 */
	public TestVertex addVertex ();
	
	
    /**
     * remove a vertex from the graph.
     * @param vertex
     */
    public boolean removeVertex(TestVertex vertex);
    
    
    /**
     * remove an edge from the graph.
     * @param edge
     */
    public boolean removeEdge(TestEdge edge);

}