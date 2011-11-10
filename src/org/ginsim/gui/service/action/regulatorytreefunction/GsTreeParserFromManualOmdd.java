package org.ginsim.gui.service.action.regulatorytreefunction;

import java.util.List;

import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;

public class GsTreeParserFromManualOmdd extends GsTreeParserFromOmdd {

	public static final String PARAM_MANUALOMDD = "p_manual_omdd";

	protected String getNodeName(int level) {
		return nodeOrder.get(level).toString();
	}

	public void init() {
		nodeOrder = (List)getParameter(PARAM_NODEORDER);
		root = (OmddNode)getParameter(PARAM_MANUALOMDD);
		initRealDepth(root);
	}

	
	/**
	 * Initialize the <b>realDepth</b> array, and <b>max_terminal</b> from an initial vertex, assuming regGraph is defined
	 * @param root
	 */
	public void initRealDepth(OmddNode root) {
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
    public void _initRealDepth(OmddNode o) {
        if (o.next == null) {
            return ;
        }
        realDetph[o.level] = -1;
        for (int i = 0 ; i < o.next.length ; i++) {
            _initRealDepth(o.next[i]);
        }
    }

}
