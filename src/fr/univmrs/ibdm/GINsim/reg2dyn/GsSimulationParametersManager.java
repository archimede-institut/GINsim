package fr.univmrs.ibdm.GINsim.reg2dyn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Vector;

import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsGraphAssociatedObjectManager;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.xml.GsXMLWriter;

/**
 * Save/open simulation parameters along with the model.
 */
public class GsSimulationParametersManager implements
        GsGraphAssociatedObjectManager {

    public void doOpen(InputStream is, GsGraph graph) {
        GsSimulationParametersParser parser = new GsSimulationParametersParser((GsRegulatoryGraph)graph);
        parser.startParsing(is, false);
        graph.addObject("reg2dyn_parameters", parser.getParameters());
    }

    public void doSave(OutputStreamWriter os, GsGraph graph) {
        GsSimulationParameterList paramList = (GsSimulationParameterList)graph.getObject("reg2dyn_parameters");
        Vector nodeOrder = graph.getNodeOrder();
        if (paramList == null || paramList.getNbElements() == 0 || nodeOrder == null || nodeOrder.size() == 0) {
            return;
        }
        try {
            GsXMLWriter out = new GsXMLWriter(os, null);
            out.openTag("simulationParameters");
            String s_nodeOrder = nodeOrder.get(0).toString();
            for (int i=1 ; i<nodeOrder.size() ; i++) {
                s_nodeOrder += " "+nodeOrder.get(i);
            }
            out.addAttr("nodeOrder", s_nodeOrder);
            for (int i=0 ; i<paramList.getNbElements() ; i++) {
                GsSimulationParameters sparam = (GsSimulationParameters)paramList.getElement(i);
                sparam.toXML(out, null, 0);
            }
            out.closeTag();
        } catch (IOException e) {
            GsEnv.error(new GsException(GsException.GRAVITY_ERROR, e.getLocalizedMessage()), null);
        }
    }

    public String getObjectName() {
        return "reg2dyn_parameters";
    }

    public boolean needSaving(GsGraph graph) {
        GsSimulationParameterList paramList = (GsSimulationParameterList)graph.getObject("reg2dyn_parameters");
        return (paramList != null && paramList.getNbElements() > 0);
    }
}
