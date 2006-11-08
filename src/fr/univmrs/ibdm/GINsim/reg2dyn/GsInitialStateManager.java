package fr.univmrs.ibdm.GINsim.reg2dyn;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Vector;

import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsGraphAssociatedObjectManager;
import fr.univmrs.ibdm.GINsim.xml.GsXMLWriter;

public class GsInitialStateManager implements GsGraphAssociatedObjectManager {

	public static final String key = "initialState";
	
    public GsInitialStateManager(GsGraph graph) {
    }

    public void doOpen(InputStream is, GsGraph graph) {
    	// FIXME: restore initial states (and adapt other parser to reuse them)
//        GsSimulationParametersParser parser = new GsSimulationParametersParser((GsRegulatoryGraph)graph);
//        parser.startParsing(is, false);
//        graph.addObject("reg2dyn_parameters", parser.getParameters());
    }

    public void doSave(OutputStreamWriter os, GsGraph graph) {
        GsInitialStateList list = (GsInitialStateList)graph.getObject(key);
        Vector nodeOrder = graph.getNodeOrder();
        if (list == null || list.getNbElements() == 0 || nodeOrder == null || nodeOrder.size() == 0) {
            return;
        }
        try {
            GsXMLWriter out = new GsXMLWriter(os, null);
            out.openTag("initialStates");
            for (int i=0 ; i<list.getNbElements() ; i++) {
            	GsInitialState is = (GsInitialState)list.getElement(i);
            	out.openTag("initialState");
            	out.addAttr("name", is.name);
            	// FIXME: save initial states
            	out.closeTag();
            }
            out.closeTag();
        } catch (IOException e) {
            GsEnv.error(new GsException(GsException.GRAVITY_ERROR, e.getLocalizedMessage()), null);
        }
    }

	public String getObjectName() {
		return key;
	}

    public boolean needSaving(GsGraph graph) {
        GsInitialStateList list = (GsInitialStateList)graph.getObject(key);
        return (list != null && list.getNbElements() > 0);
    }
}
