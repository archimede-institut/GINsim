package fr.univmrs.ibdm.GINsim.regulatoryGraph;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Vector;

import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsGraphAssociatedObjectManager;
import fr.univmrs.ibdm.GINsim.xml.GsXMLWriter;

/**
 * Save/open simulation parameters along with the model.
 */
public class GsMutantListManager implements
        GsGraphAssociatedObjectManager {

    public void doOpen(InputStream is, GsGraph graph) {
        GsRegulatoryMutantParser parser = new GsRegulatoryMutantParser((GsRegulatoryGraph)graph);
        parser.startParsing(is, false);
    }

    public void doSave(OutputStreamWriter os, GsGraph graph) {
        GsRegulatoryMutants lMutant = (GsRegulatoryMutants)graph.getObject("mutant");
        Vector nodeOrder = graph.getNodeOrder();
        if (lMutant == null || lMutant.getNbElements() == 0 || nodeOrder == null || nodeOrder.size() == 0) {
            return;
        }
        try {
            GsXMLWriter out = new GsXMLWriter(os, null);
            out.openTag("mutantList");
            for (int i=0 ; i<lMutant.getNbElements() ; i++) {
                GsRegulatoryMutantDef mutant = (GsRegulatoryMutantDef)lMutant.getElement(i);
                mutant.toXML(out);
            }
            out.closeTag();
        } catch (IOException e) {
            GsEnv.error(new GsException(GsException.GRAVITY_ERROR, e.getLocalizedMessage()), null);
        }
    }

    public String getObjectName() {
        return "mutant";
    }

    public boolean needSaving(GsGraph graph) {
        GsRegulatoryMutants lMutant = (GsRegulatoryMutants)graph.getObject("mutant");
        return (lMutant != null && lMutant.getNbElements() > 0);
    }
}
