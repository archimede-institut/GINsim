package org.ginsim.service.tool.sccgraph;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.service.Service;
import org.mangosdk.spi.ProviderFor;

@ProviderFor(Service.class)
public class SCCGraphService implements Service {

	/**
	 * Compute the SCC of graph.
	 * 
	 * 
	 * @param graph the graph to compute the SCC on
	 * @return
	 */
	public SCCGraphResult run(Graph graph) {
		SCCGraphAlgo algo = new SCCGraphAlgo();
		SCCGraphResult result = algo.configure(graph);
		algo.run();
		return result;
	}
	
}
