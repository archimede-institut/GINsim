package fr.univmrs.tagc.GINsim.regulatoryGraph;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.regulatorygraph.GsRegulatoryGraph;
import org.ginsim.gui.GUIManager;

import fr.univmrs.tagc.GINsim.graph.GsGraphAssociatedObjectManager;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutantDef;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutantParser;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutants;
import fr.univmrs.tagc.common.xml.XMLWriter;

/**
 * Save/open simulation parameters along with the model.
 */
public class GsMutantListManager implements
        GsGraphAssociatedObjectManager {

	public static final String key = "mutant";
	
    public Object doOpen(InputStream is, Graph graph) {
    	
        GsRegulatoryMutantParser parser = new GsRegulatoryMutantParser((GsRegulatoryGraph) graph);
        parser.startParsing(is, false);
        return parser.getParameters();
    }

    public void doSave(OutputStreamWriter os, Graph graph) {
    	
        GsRegulatoryMutants lMutant = (GsRegulatoryMutants)graph.getObject(key, false);
        List nodeOrder = ((GsRegulatoryGraph)graph).getNodeOrder();
        if (lMutant == null || lMutant.getNbElements(null) == 0 || nodeOrder == null || nodeOrder.size() == 0) {
            return;
        }
        try {
            XMLWriter out = new XMLWriter(os, null);
            out.openTag("mutantList");
            for (int i=0 ; i<lMutant.getNbElements(null) ; i++) {
                GsRegulatoryMutantDef mutant = (GsRegulatoryMutantDef)lMutant.getElement(null, i);
                mutant.toXML(out);
            }
            out.closeTag();
        } catch (IOException e) {
            GUIManager.getInstance().error(new GsException(GsException.GRAVITY_ERROR, e.getLocalizedMessage()), null);
        }
    }

    public String getObjectName() {
        return "mutant";
    }

    public boolean needSaving( Graph graph) {
    	
        GsRegulatoryMutants lMutant = (GsRegulatoryMutants) graph.getObject("mutant", false);
        return lMutant != null && lMutant.getNbElements(null) > 0;
    }

	public Object doCreate( Graph graph) {
		
		return new GsRegulatoryMutants( (GsRegulatoryGraph)graph);
	}
}
