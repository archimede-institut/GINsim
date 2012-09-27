package org.ginsim.service.tool.composition;

import java.util.List;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.service.tool.modelsimplifier.ReductionLauncher;
import org.ginsim.service.tool.modelsimplifier.RemovedInfo;

public class ReductionStub implements ReductionLauncher {
	private RegulatoryGraph graph = null;
	private RegulatoryGraph reducedGraph = null;

	public ReductionStub(RegulatoryGraph graph) {
		this.graph = graph;
		
	}

	@Override
	public void endSimu(Graph graph, Exception e) {
		this.reducedGraph = (RegulatoryGraph) graph;

	}

	@Override
	public boolean showPartialReduction(List<RemovedInfo> l_todo) {
		return false;
	}
	
	public RegulatoryGraph getReducedGraph(){
		return this.reducedGraph;
	}

}
