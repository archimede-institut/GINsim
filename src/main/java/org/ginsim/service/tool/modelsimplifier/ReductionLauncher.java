package org.ginsim.service.tool.modelsimplifier;

import java.util.List;

import org.ginsim.core.graph.common.Graph;

public interface ReductionLauncher {

	void endSimu(Graph graph, Exception e);

	boolean showPartialReduction(List<RemovedInfo> l_todo);

}
