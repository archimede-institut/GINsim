package org.ginsim.core.graph.tree;

import java.util.List;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;
import org.ginsim.core.mdd.OmsddNode;

public class TreeBuilderFromRegulatoryGraph extends TreeBuilderFromOmdd {
	
	public static final String PARAM_REGGRAPH = "pfrg_regGraph";
	public static final String PARAM_INITIALVERTEXINDEX = "pfrg_initialNode";
	
	protected RegulatoryGraph regGraph;


	public void init() {
		int initial_gene_id = ((Integer)getParameter(PARAM_INITIALVERTEXINDEX)).intValue();
		nodeOrder = (List<RegulatoryNode>)getParameter(PARAM_NODEORDER);
		regGraph = (RegulatoryGraph)getParameter(PARAM_REGGRAPH);

		RegulatoryNode initialNode = nodeOrder.get(initial_gene_id);
		
		this.root = initialNode.getTreeParameters(regGraph).reduce();
		widthPerDepth = widthPerDepth_acc = realDetph = null;
		total_levels = max_depth = 0;
		max_terminal = initialNode.getMaxValue()+1;
		initRealDepth(root);
	}
	/**
	 * Initialize the <b>realDepth</b> array, and <b>max_terminal</b> from an initial node, assuming regGraph is defined
	 * @param root
	 */
	public void initRealDepth(OMDDNode root) {
		realDetph = new int[nodeOrder.size()+1]; //+1 for the leafs
		_initRealDepth(root);
		int next_realDepth = 0;
		for (int i = 0; i < realDetph.length; i++) {
			if (realDetph[i] == -1) {
				total_levels++;
				realDetph[i] = next_realDepth++;
			} else realDetph[i] = -2;
		}
	}
    public void _initRealDepth(OMDDNode o) {
        if (o.next == null) {
            return ;
        }
        realDetph[o.level] = -1;
        for (int i = 0 ; i < o.next.length ; i++) {
            _initRealDepth(o.next[i]);
        }
    }

    protected String getNodeName(int level) {
		return nodeOrder.get(level).getId();
	}

}