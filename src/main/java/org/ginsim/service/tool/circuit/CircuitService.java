package org.ginsim.service.tool.circuit;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.*;
import org.mangosdk.spi.ProviderFor;

import java.util.List;

@ProviderFor( Service.class)
@Alias("circuitAnalysis")
@ServiceStatus(EStatus.RELEASED)
public class CircuitService implements Service {

    public CircuitAlgo getCircuitAnalyser(RegulatoryGraph graph) {
        return getCircuitAnalyser(graph, false);
    }

    public CircuitAlgo getCircuitAnalyser(RegulatoryGraph graph, boolean doCleanup) {
        return new CircuitAlgo(graph, doCleanup);
    }

    public CircuitSearcher getCircuitSearcher(RegulatoryGraph graph) {
        return new CircuitSearcher(graph);
    }

    public List<CircuitDescrInTree> getCircuits(RegulatoryGraph graph) {
        return getCircuitSearcher(graph).call();
    }

}
