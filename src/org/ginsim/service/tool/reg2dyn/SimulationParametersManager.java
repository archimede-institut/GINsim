package org.ginsim.service.tool.reg2dyn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.ginsim.common.exception.GsException;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.common.xml.XMLize;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.objectassociation.GraphAssociatedObjectManager;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;


/**
 * Save/open simulation parameters along with the model.
 */
public class SimulationParametersManager implements GraphAssociatedObjectManager {

	public static final String key = "reg2dyn_parameters";
	
    public Object doOpen(InputStream is, Graph graph)  throws GsException{
        SimulationParametersParser parser = new SimulationParametersParser((RegulatoryGraph)graph);
        parser.startParsing(is, false);
        return parser.getParameters();
    }

    public void doSave(OutputStreamWriter os, Graph graph) throws GsException{
        SimulationParameterList paramList = (SimulationParameterList) ObjectAssociationManager.getInstance().getObject( graph, key, false);
        List nodeOrder = ((RegulatoryGraph)graph).getNodeOrder();
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
                SimulationParameters sparam = (SimulationParameters)paramList.getElement(null, i);
                sparam.toXML(out, null, 0);
            }
            out.closeTag();
        } catch (IOException e) {
            throw new GsException(GsException.GRAVITY_ERROR, e.getLocalizedMessage());
        }
    }

    public String getObjectName() {
        return key;
    }

    public boolean needSaving( Graph graph) {
        SimulationParameterList paramList = (SimulationParameterList)  ObjectAssociationManager.getInstance().getObject( graph, key, false);
        return paramList != null && (paramList.getNbElements(null) > 0 || 
        		paramList.pcmanager != null && paramList.pcmanager.getNbElements(null) > 2);
    }

	public Object doCreate( Graph graph) {
		return new SimulationParameterList( graph);
	}
}
