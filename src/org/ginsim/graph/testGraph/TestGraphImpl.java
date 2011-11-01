package org.ginsim.graph.testGraph;

import org.ginsim.graph.AbstractGraphFrontend;

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
}
