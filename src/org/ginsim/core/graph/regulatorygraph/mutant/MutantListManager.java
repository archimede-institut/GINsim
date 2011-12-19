package org.ginsim.core.graph.regulatorygraph.mutant;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.ginsim.common.exception.GsException;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.objectassociation.GraphAssociatedObjectManager;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;


/**
 * Save/open simulation parameters along with the model.
 */
public class MutantListManager implements
        GraphAssociatedObjectManager {

	public static final String key = "mutant";
	
    public Object doOpen(InputStream is, Graph graph) {
    	
        RegulatoryMutantParser parser = new RegulatoryMutantParser((RegulatoryGraph) graph);
        parser.startParsing(is, false);
        return parser.getParameters();
    }

    public void doSave(OutputStreamWriter os, Graph graph) throws GsException{
    	
        RegulatoryMutants lMutant = (RegulatoryMutants) ObjectAssociationManager.getInstance().getObject(graph, key, false);
        List nodeOrder = ((RegulatoryGraph)graph).getNodeOrder();
        if (lMutant == null || lMutant.getNbElements(null) == 0 || nodeOrder == null || nodeOrder.size() == 0) {
            return;
        }
        try {
            XMLWriter out = new XMLWriter(os, null);
            out.openTag("mutantList");
            for (int i=0 ; i<lMutant.getNbElements(null) ; i++) {
                RegulatoryMutantDef mutant = (RegulatoryMutantDef)lMutant.getElement(null, i);
                mutant.toXML(out);
            }
            out.closeTag();
        } catch (IOException e) {
            throw new GsException( "STR_unableToSave", e);
        }
    }

    public String getObjectName() {
        return "mutant";
    }

    public boolean needSaving( Graph graph) {
    	
        RegulatoryMutants lMutant = (RegulatoryMutants) ObjectAssociationManager.getInstance().getObject(graph, "mutant", false);
        return lMutant != null && lMutant.getNbElements(null) > 0;
    }

	public Object doCreate( Graph graph) {
		
		return new RegulatoryMutants( (RegulatoryGraph)graph);
	}
}
