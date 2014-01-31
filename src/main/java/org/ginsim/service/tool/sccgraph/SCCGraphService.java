package org.ginsim.service.tool.sccgraph;

import org.colomoto.common.task.Task;
import org.colomoto.common.task.TaskListener;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.reducedgraph.ReducedGraph;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.EStatus;
import org.ginsim.core.service.Service;
import org.ginsim.core.service.ServiceStatus;
import org.mangosdk.spi.ProviderFor;

@ProviderFor(Service.class)
@Alias("SCC")
@ServiceStatus(EStatus.RELEASED)
public class SCCGraphService implements Service {

    /**
     * Compute the SCC of graph.
     *
     * @param graph the graph to compute the SCC on
     * @return an object containing the resulting SCC graph
     */
    public ReducedGraph call(Graph graph) {
        SCCGraphAlgo algo = new SCCGraphAlgo(graph);
        return algo.call();
    }

    /**
     * Compute the SCC graph in background.
     *
     * @param graph
     */
    public Task<ReducedGraph> background(Graph graph, TaskListener listener) {
        SCCGraphAlgo algo = new SCCGraphAlgo(graph);
        algo.background(listener);
        return algo;
    }
}
