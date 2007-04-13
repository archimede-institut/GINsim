package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.models.GsTableInteractionsModel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import java.util.Vector;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;

public class GsFTableInteractionModel extends GsTableInteractionsModel {
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
