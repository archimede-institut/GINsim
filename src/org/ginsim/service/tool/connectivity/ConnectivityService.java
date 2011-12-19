package org.ginsim.service.tool.connectivity;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.service.Service;
import org.mangosdk.spi.ProviderFor;

@ProviderFor(Service.class)
public class ConnectivityService implements Service {

	/**
	 * Compute the SCC of graph.
	 * 
	 * 
	 * @param graph the graph to compute the SCC on
     * @param searchMode MODE_COMPO=only find components; MODE_FULL=also search for path and create the reduced graph
	 * @return
	 */
	public ConnectivityResult run(Graph graph) {
		ConnectivityAlgo algo = new ConnectivityAlgo();
		ConnectivityResult result = algo.configure(graph);
		algo.run();
		return result;
	}
	
}
