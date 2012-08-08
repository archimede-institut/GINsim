package org.ginsim.core.graph.regulatorygraph.perturbation;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.logicalmodel.perturbation.AbstractPerturbation;
import org.colomoto.mddlib.MDDManager;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialState;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;



/**
 * A perturbation, based on an initial state definition.
 * It is meant to be used to restrict ourself to some input conditions: it will replace the MDD such that only values allowed by the initial state will be stable.
 * All other values are replaced by the closest lower allowed value (or closest higher for the first values).
 */
public class InitBasedPerturbation extends AbstractPerturbation implements Perturbation {

    InitialState init;
    
    public InitBasedPerturbation(InitialState init) {
        this.init = init;
    }
    
    public void apply(OMDDNode[] t_tree, RegulatoryGraph graph) {
    	throw new RuntimeException("Unsupported perturbation application");
    }

	@Override
	public void update(LogicalModel model) {
		MDDManager factory = model.getMDDManager();
		int[] nodes = model.getLogicalFunctions();
		List<NodeInfo> order = model.getNodeOrder();
		
        Map<NodeInfo, List<Integer>> m_init = init.getMap();
        for (Entry<NodeInfo, List<Integer>> e: m_init.entrySet()) {
        	NodeInfo vertex = e.getKey();
            List<Integer> values = e.getValue();
            if (values == null || values.size() < 1) {
                continue; // nothing to apply here
            }
            int min = vertex.getMax();
            boolean[] tvals = new boolean[vertex.getMax()+1];
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
            int newnode = factory.getVariableForKey(vertex).getNode(next);
            factory.free(nodes[index]);
            nodes[index] = newnode;
        }
	}

	@Override
	public void toXML(XMLWriter out) throws IOException {
		// TODO Auto-generated method stub
		
	}
}
