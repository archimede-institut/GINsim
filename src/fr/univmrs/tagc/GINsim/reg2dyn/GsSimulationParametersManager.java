package fr.univmrs.tagc.GINsim.reg2dyn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.ginsim.exception.GsException;
import org.ginsim.graph.Graph;

import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.GINsim.graph.GsGraphAssociatedObjectManager;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.common.xml.XMLWriter;
import fr.univmrs.tagc.common.xml.XMLize;

/**
 * Save/open simulation parameters along with the model.
 */
public class GsSimulationParametersManager implements GsGraphAssociatedObjectManager {

	public static final String key = "reg2dyn_parameters";
	
    public Object doOpen(InputStream is, Graph graph) {
        GsSimulationParametersParser parser = new GsSimulationParametersParser((GsRegulatoryGraph)graph);
        parser.startParsing(is, false);
        return parser.getParameters();
    }

    public void doSave(OutputStreamWriter os, Graph graph) {
        GsSimulationParameterList paramList = (GsSimulationParameterList)graph.getObject(key, false);
        List nodeOrder = graph.getNodeOrder();
        if (paramList == null || paramList.getNbElements(null) == 0 || nodeOrder == null || nodeOrder.size() == 0) {
            return;
        }
        try {
            XMLWriter out = new XMLWriter(os, null);
            out.openTag("simulationParameters");
            String s_nodeOrder = nodeOrder.get(0).toString();
            for (int i=1 ; i<nodeOrder.size() ; i++) {
                s_nodeOrder += " "+nodeOrder.get(i);
            }
            out.addAttr("nodeOrder", s_nodeOrder);
            // add priority class definition
            if (paramList.pcmanager != null && paramList.pcmanager.getNbElements(null) > 0) {
                for (int i=2 ; i<paramList.pcmanager.getNbElements(null) ; i++) {
                	((XMLize)paramList.pcmanager.getElement(null, i)).toXML(out, null, 0);
                }
            }
            // and the real parameters
            for (int i=0 ; i<paramList.getNbElements(null) ; i++) {
                GsSimulationParameters sparam = (GsSimulationParameters)paramList.getElement(null, i);
                sparam.toXML(out, null, 0);
            }
            out.closeTag();
        } catch (IOException e) {
            GsEnv.error(new GsException(GsException.GRAVITY_ERROR, e.getLocalizedMessage()), null);
        }
    }

    public String getObjectName() {
        return key;
    }

    public boolean needSaving( Graph graph) {
        GsSimulationParameterList paramList = (GsSimulationParameterList)graph.getObject(key, false);
        return paramList != null && (paramList.getNbElements(null) > 0 || 
        		paramList.pcmanager != null && paramList.pcmanager.getNbElements(null) > 2);
    }

	public Object doCreate( Graph graph) {
		return new GsSimulationParameterList( graph);
	}
}
