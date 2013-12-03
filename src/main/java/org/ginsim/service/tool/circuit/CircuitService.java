package org.ginsim.service.tool.circuit;

import org.ginsim.core.graph.reducedgraph.NodeReducedData;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.Service;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.service.tool.connectivity.ConnectivityResult;
import org.ginsim.service.tool.connectivity.ConnectivityService;
import org.mangosdk.spi.ProviderFor;

import java.util.List;

@ProviderFor( Service.class)
@Alias("circuitAnalysis")
public class CircuitService implements Service {

    ConnectivityService connectivity = null;


    CircuitAlgo getCircuitAnalyser(RegulatoryGraph graph) {
        return getCircuitAnalyser(graph, false);
    }

    CircuitAlgo getCircuitAnalyser(RegulatoryGraph graph, boolean doCleanup) {
        return new CircuitAlgo(graph, doCleanup);
    }

    private void findCircuits(RegulatoryGraph graph) {
        if (connectivity == null) {
            connectivity = ServiceManager.getManager().getService(ConnectivityService.class);
            if (connectivity == null) {
                throw new RuntimeException("Circuit analysis requires the connectivity service");
            }
        }

        List<NodeReducedData> sccs = connectivity.getSCC(graph).getComponents();


    }
}
