package org.ginsim.core.graph.regulatorygraph.mutant;

import java.util.ArrayList;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;

/**
 * A list of perturbations, allowing to apply several of them at the same time without duplication.
 */
public class PerturbationList extends ArrayList<Perturbation> implements Perturbation {
    private static final long serialVersionUID = 6186448725402623972L;

    public void apply(OMDDNode[] t_tree, RegulatoryGraph graph) {
        for (Perturbation p: this) {
            p.apply(t_tree, graph);
        }
    }
}
