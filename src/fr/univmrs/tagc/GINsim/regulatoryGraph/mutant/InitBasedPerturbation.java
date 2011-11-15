package fr.univmrs.tagc.GINsim.regulatoryGraph.mutant;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ginsim.graph.regulatorygraph.GsRegulatoryGraph;
import org.ginsim.graph.regulatorygraph.GsRegulatoryVertex;

import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialState;

/**
 * A perturbation, based on an initial state definition.
 * It is meant to be used to restrict ourself to some input conditions: it will replace the MDD such that only values allowed by the initial state will be stable.
 * All other values are replaced by the closest lower allowed value (or closest higher for the first values).
 */
public class InitBasedPerturbation implements Perturbation {
    private static final long serialVersionUID = 6186448725402623972L;

    GsInitialState init;
    
    public InitBasedPerturbation(GsInitialState init) {
        this.init = init;
    }
    
    public void apply(OmddNode[] t_tree, GsRegulatoryGraph graph) {
        Map m_init = init.getMap();
        List norder = graph.getNodeOrder();
        for (Iterator it=m_init.entrySet().iterator() ; it.hasNext() ;) {
            Entry e = (Entry)it.next();
            GsRegulatoryVertex vertex = (GsRegulatoryVertex)e.getKey();
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
            OmddNode node = new OmddNode();
            node.level = index;
            node.next = new OmddNode[tvals.length];
            for (int pos=0 ; pos<tvals.length ; pos++) {
                if (tvals[pos]) {
                    min = pos;
                    node.next[pos] = OmddNode.TERMINALS[pos];
                } else {
                    node.next[pos] = OmddNode.TERMINALS[min];
                }
            }
            t_tree[index] = node;
        }
    }
}
