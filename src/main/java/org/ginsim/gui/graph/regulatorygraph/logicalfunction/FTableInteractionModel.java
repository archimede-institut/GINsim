package org.ginsim.gui.graph.regulatorygraph.logicalfunction;

import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.gui.graph.regulatorygraph.TableInteractionsModel;


public class FTableInteractionModel extends TableInteractionsModel {
	private static final long serialVersionUID = 6944736425274853595L;

    public FTableInteractionModel(RegulatoryNode no) {
        super(no);
    }
    public int getRowCount() {
        if (getInteractions() == null)
            return 0;
        return getInteractions().size();
    }
}
