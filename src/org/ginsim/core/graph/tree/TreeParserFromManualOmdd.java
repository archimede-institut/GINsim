package org.ginsim.core.graph.tree;

import java.util.List;

import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;


public class TreeParserFromManualOmdd extends TreeParserFromOmdd {

	public static final String PARAM_MANUALOMDD = "p_manual_omdd";

	protected String getNodeName(int level) {
		return nodeOrder.get(level).toString();
	}

	public void init() {
		nodeOrder = (List)getParameter(PARAM_NODEORDER);
		root = (OMDDNode)getParameter(PARAM_MANUALOMDD);
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

}
