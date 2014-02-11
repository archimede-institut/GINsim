package org.ginsim.service.tool.circuit;

import org.colomoto.common.task.AbstractTask;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.perturbation.Perturbation;

import java.util.List;

/**
 * A Task for circuit analysis: returns a list of circuits with functionality contexts.
 *
 * @author Aurelien Naldi
 */
public class CircuitAnalyser extends AbstractTask<List<CircuitDescrInTree>> {

    private final List<CircuitDescrInTree> circuits;
    private final CircuitSearcher searcher;

    private final CircuitAlgo algo;
    private final List<RegulatoryNode> nodeOrder;


    public CircuitAnalyser(RegulatoryGraph graph, Perturbation perturbation, boolean do_cleanup) {
        this(graph, perturbation, do_cleanup, null);
    }

    public CircuitAnalyser(RegulatoryGraph graph, Perturbation perturbation, boolean do_cleanup, List<CircuitDescrInTree> circuits) {
        this.circuits = circuits;
        if (circuits == null) {
            searcher = new CircuitSearcher(graph);
        } else {
            searcher = null;
        }
        this.algo = new CircuitAlgo(graph, perturbation, do_cleanup);
        this.nodeOrder = graph.getNodeOrder();
    }

    @Override
    protected List<CircuitDescrInTree> doGetResult() throws Exception {
        List<CircuitDescrInTree> circuits = this.circuits;
        if (circuits == null) {
            circuits = searcher.call();
        }

        for (CircuitDescrInTree cdt: circuits) {
            CircuitDescr cd = cdt.getCircuit();
            cd.check(algo, nodeOrder);
        }

        return circuits;
    }
}
