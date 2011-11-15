package fr.univmrs.tagc.GINsim.regulatoryGraph.mutant;

import java.util.ArrayList;
import java.util.Iterator;

import org.ginsim.graph.regulatorygraph.GsRegulatoryGraph;

import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;

/**
 * A list of perturbations, allowing to apply several of them at the same time without duplication.
 */
public class PerturbationList extends ArrayList implements Perturbation {
    private static final long serialVersionUID = 6186448725402623972L;

    public void apply(OmddNode[] t_tree, GsRegulatoryGraph graph) {
        for (Iterator it = iterator() ; it.hasNext() ; ) {
            Perturbation p = (Perturbation)it.next();
            p.apply(t_tree, graph);
        }
    }
}
