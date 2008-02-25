package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.GINsim.regulatoryGraph.models.GsTableInteractionsModel;

public class GsFTableInteractionModel extends GsTableInteractionsModel {
	private static final long serialVersionUID = 6944736425274853595L;
	/* public GsFTableInteractionModel(GsRegulatoryGraph graph, Vector v_ok) {
        super(graph, v_ok);
    }*/
    public GsFTableInteractionModel(GsRegulatoryVertex no) {
        super(no);
    }
    public int getRowCount() {
        if (getInteractions() == null)
            return 0;
        return getInteractions().size();
    }
}
