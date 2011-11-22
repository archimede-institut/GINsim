package org.ginsim.gui.graph.regulatorygraph.logicalfunction;

import org.ginsim.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.gui.graph.regulatorygraph.models.TableInteractionsModel;


public class FTableInteractionModel extends TableInteractionsModel {
	private static final long serialVersionUID = 6944736425274853595L;
	/* public FTableInteractionModel(RegulatoryGraph graph, Vector v_ok) {
        super(graph, v_ok);
    }*/
    public FTableInteractionModel(RegulatoryNode no) {
        super(no);
    }
    public int getRowCount() {
        if (getInteractions() == null)
            return 0;
        return getInteractions().size();
    }
}
