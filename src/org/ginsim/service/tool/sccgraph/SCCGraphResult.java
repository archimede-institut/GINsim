package org.ginsim.service.tool.sccgraph;

import org.ginsim.core.graph.reducedgraph.ReducedGraph;

/**
 * Store the result of the connectivity Algorithm :
 *   - The reduced graph of the SCC : ReducedGraph reducedGraph
 */
public class SCCGraphResult {
	private ReducedGraph reducedGraph;

	public SCCGraphResult() {
		this.reducedGraph = null;
	}
	
	public void setReducedGraph(ReducedGraph reducedGraph) {
		this.reducedGraph = reducedGraph;
	}
	
	public ReducedGraph getReducedGraph() {
		return reducedGraph;
	}
}
