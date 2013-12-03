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

    public CircuitAlgo getCircuitAnalyser(RegulatoryGraph graph) {
        return getCircuitAnalyser(graph, false);
    }

    public CircuitAlgo getCircuitAnalyser(RegulatoryGraph graph, boolean doCleanup) {
        return new CircuitAlgo(graph, doCleanup);
    }

    public List<CircuitDescrInTree> getCircuits(RegulatoryGraph graph) {
        CircuitSearcher csearcher = new CircuitSearcher(graph);
        return csearcher.getCircuits();
    }

}
