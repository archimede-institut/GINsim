package org.ginsim.core.graph.regulatorygraph.mutant;

import java.util.ArrayList;
import java.util.List;

import org.ginsim.core.graph.common.NodeInfo;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;

import fr.univmrs.tagc.javaMDD.MDDFactory;

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

	@Override
	public int[] apply(MDDFactory factory, int[] nodes, RegulatoryGraph graph) {
		List<NodeInfo> nodeInfo = graph.getNodeInfos();
		return apply(factory, nodes, nodeInfo);
	}

	@Override
	public int[] apply(MDDFactory factory, int[] nodes, List<NodeInfo> nodeInfo) {
		int[] perturbed = nodes;
        for (Perturbation p: this) {
            perturbed = p.apply(factory, perturbed, nodeInfo);
        }
		return perturbed;
	}
}
