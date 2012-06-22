package org.ginsim.core.graph.regulatorygraph.mutant;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.colomoto.mddlib.MDDManager;
import org.ginsim.core.graph.common.NodeInfo;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialState;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;
import org.ginsim.core.logicalmodel.LogicalModel;



/**
 * A perturbation, based on an initial state definition.
 * It is meant to be used to restrict ourself to some input conditions: it will replace the MDD such that only values allowed by the initial state will be stable.
 * All other values are replaced by the closest lower allowed value (or closest higher for the first values).
 */
public class InitBasedPerturbation implements Perturbation {

    InitialState init;
    
    public InitBasedPerturbation(InitialState init) {
        this.init = init;
    }
    
    public void apply(OMDDNode[] t_tree, RegulatoryGraph graph) {
        Map<RegulatoryNode, List<Integer>> m_init = init.getMap();
        List<RegulatoryNode> norder = graph.getNodeOrder();
        for (Entry<RegulatoryNode, List<Integer>> e: m_init.entrySet()) {
            RegulatoryNode vertex = e.getKey();
            List<Integer> values = e.getValue();
            if (values == null || values.size() < 1) {
                continue; // nothing to apply here
            }
            int min = vertex.getMaxValue();
            boolean[] tvals = new boolean[vertex.getMaxValue()+1];
            // set all values in the array
            for (int v: values ) {
                if (v < min) {
                    min = v;
                }
                tvals[v] = true;
            }

            // replace the MDD
            int index  = norder.indexOf(vertex);
            OMDDNode node = new OMDDNode();
            node.level = index;
            node.next = new OMDDNode[tvals.length];
            for (int pos=0 ; pos<tvals.length ; pos++) {
                if (tvals[pos]) {
                    min = pos;
                    node.next[pos] = OMDDNode.TERMINALS[pos];
                } else {
                    node.next[pos] = OMDDNode.TERMINALS[min];
                }
            }
            t_tree[index] = node;
        }
    }

	@Override
	public void apply(LogicalModel model) {
		MDDManager factory = model.getMDDFactory();
		int[] nodes = model.getLogicalFunctions();
		List<NodeInfo> order = model.getNodeOrder();
		
        Map<RegulatoryNode, List<Integer>> m_init = init.getMap();
        for (Entry<RegulatoryNode, List<Integer>> e: m_init.entrySet()) {
            RegulatoryNode vertex = e.getKey();
            List<Integer> values = e.getValue();
            if (values == null || values.size() < 1) {
                continue; // nothing to apply here
            }
            int min = vertex.getMaxValue();
            boolean[] tvals = new boolean[vertex.getMaxValue()+1];
            // set all values in the array
            for (int v: values ) {
                if (v < min) {
                    min = v;
                }
                tvals[v] = true;
            }

            // replace the MDD
            int index  = order.indexOf(vertex);
            int[] next = new int[tvals.length];
            for (int pos=0 ; pos<tvals.length ; pos++) {
                if (tvals[pos]) {
                    min = pos;
                    next[pos] = pos;
                } else {
                    next[pos] = min;
                }
            }
            int newnode = factory.get_mnode(factory.getVariableID(vertex), next);
            factory.free(nodes[index]);
            nodes[index] = newnode;
        }
	}
}
