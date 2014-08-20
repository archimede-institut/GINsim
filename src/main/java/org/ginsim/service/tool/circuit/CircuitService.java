package org.ginsim.service.tool.circuit;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.perturbation.Perturbation;
import org.ginsim.core.service.*;
import org.mangosdk.spi.ProviderFor;


@ProviderFor( Service.class)
@Alias("circuitAnalysis")
@ServiceStatus(EStatus.RELEASED)
public class CircuitService implements Service {

    public CircuitAnalyser getCircuitAnalyser(RegulatoryGraph graph) {
        return getCircuitAnalyser(graph, false);
    }

    public CircuitAnalyser getCircuitAnalyser(RegulatoryGraph graph, boolean doCleanup) {
        return getCircuitAnalyser(graph, null, doCleanup);
    }

    public CircuitAnalyser getCircuitAnalyser(RegulatoryGraph graph, Perturbation perturbation, boolean doCleanup) {
        return new CircuitAnalyser(graph, perturbation, doCleanup);
    }

    public CircuitSearcher getCircuitSearcher(RegulatoryGraph graph, CircuitSearchStoreConfig config) {
        return new CircuitSearcher(graph, config);
    }

}
