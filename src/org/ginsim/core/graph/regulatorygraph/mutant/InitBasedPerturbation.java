package org.ginsim.core.graph.regulatorygraph.mutant;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialState;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;


/**
 * A perturbation, based on an initial state definition.
 * It is meant to be used to restrict ourself to some input conditions: it will replace the MDD such that only values allowed by the initial state will be stable.
 * All other values are replaced by the closest lower allowed value (or closest higher for the first values).
 */
public class InitBasedPerturbation implements Perturbation {
    private static final long serialVersionUID = 6186448725402623972L;

    InitialState init;
    
    public InitBasedPerturbation(InitialState init) {
        this.init = init;
    }
    
    public void apply(OMDDNode[] t_tree, RegulatoryGraph graph) {
        Map m_init = init.getMap();
        List norder = graph.getNodeOrder();
        for (Iterator it=m_init.entrySet().iterator() ; it.hasNext() ;) {
            Entry e = (Entry)it.next();
            RegulatoryNode vertex = (RegulatoryNode)e.getKey();
            List values = (List)e.getValue();
            if (values == null || values.size() < 1) {
                continue; // nothing to apply here
            }
            int min = vertex.getMaxValue();
            boolean[] tvals = new boolean[vertex.getMaxValue()+1];
            // set all values in the array
            for (Iterator it2=values.iterator() ; it2.hasNext() ; ) {
                int v = ((Integer)it.next()).intValue();
                if (v<min) {
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
}
