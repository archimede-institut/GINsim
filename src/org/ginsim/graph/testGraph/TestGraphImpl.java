package org.ginsim.graph.testGraph;

import java.util.Collection;
import java.util.List;

import org.ginsim.graph.AbstractGraphFrontend;
import org.ginsim.graph.Graph;

/**
 * Simple LRG, mostly placeholder for now.
 * 
 * @author Aurelien Naldi
 */
public class TestGraphImpl extends AbstractGraphFrontend<TestVertex, TestEdge> implements TestGraph {

	@Override
	protected TestVertex createVertex(int mode) {
		return new TestVertex();
	}

	@Override
	protected TestEdge createEdge(TestVertex source, TestVertex target, int mode) {
		return new TestEdge(source, target);
	}

	
	@Override
	public void specificMethod() {
		// TODO: stuff... 
	}

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
}
