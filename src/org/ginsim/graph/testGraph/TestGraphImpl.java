package org.ginsim.graph.testGraph;

import java.util.Collection;
import java.util.List;

import org.ginsim.graph.common.AbstractGraphFrontend;
import org.ginsim.graph.common.Graph;

/**
 * Simple LRG, mostly placeholder for now.
 * 
 * @author Aurelien Naldi
 */
public class TestGraphImpl extends AbstractGraphFrontend<TestVertex, TestEdge> implements TestGraph {

	@Override
	protected List doMerge(Graph<TestVertex, TestEdge> graph) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Graph<TestVertex, TestEdge> getSubgraph(
			Collection<TestVertex> vertex, Collection<TestEdge> edges) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public TestVertex addVertex() {
		TestVertex vertex = new TestVertex();
		super.addVertex(vertex);
		return vertex;
	}
	
    @Override
    public List getSpecificObjectManager() {
    	
        return null;
    }
    
    @Override
	public int getNodeOrderSize(){
		
		return 0;
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
	public TestEdge addEdge(TestVertex source, TestVertex target) {
		TestEdge edge = new TestEdge(source, target);
		super.addEdge(edge);
		return edge;
	}

}
