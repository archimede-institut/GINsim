package org.ginsim.core.graph.regulatorygraph.initialstate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.ginsim.common.xml.XMLHelper;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.exception.GsException;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.objectassociation.GraphAssociatedObjectManager;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.GUIManager;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


public class InitialStateManager implements GraphAssociatedObjectManager {

	public static final String key = "initialState";
	
    public Object doOpen(InputStream is, Graph graph) {
    	
        initStateParser parser = new initStateParser((RegulatoryGraph)graph);
        parser.startParsing(is, false);
        return parser.getParameters();
    }

    public void doSave(OutputStreamWriter os, Graph graph) {
        GsInitialStateList imanager = (GsInitialStateList) ObjectAssociationManager.getInstance().getObject(graph, key, true);
        List nodeOrder = ((RegulatoryGraph)graph).getNodeOrder();
        if (imanager == null || imanager.isEmpty() || nodeOrder == null || nodeOrder.size() == 0) {
            return;
        }
        try {
            XMLWriter out = new XMLWriter(os, null);
            out.openTag("initialStates");
            imanager.getInitialStates().toXML(out, "initialState");
            imanager.getInputConfigs().toXML(out, "input");
            out.closeTag();
        } catch (IOException e) {
            GUIManager.error(new GsException(GsException.GRAVITY_ERROR, e.getLocalizedMessage()), null);
        }
    }

	public String getObjectName() {
		return key;
	}

    public boolean needSaving( Graph graph) {
        GsInitialStateList list = (GsInitialStateList) ObjectAssociationManager.getInstance().getObject(graph, key, false);
        return list != null && !list.isEmpty();
    }

	public Object doCreate( Graph graph) {
		return new GsInitialStateList(graph);
	}
}


class initStateParser extends XMLHelper {

    GsInitialStateList imanager;
    InitialStateList list, inputs;
    
    /**
     * @param graph expected node order
     */
    public initStateParser(RegulatoryGraph graph) {
        imanager = new GsInitialStateList( graph);
        list = imanager.getInitialStates();
        inputs = imanager.getInputConfigs();
    }
    
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        
        if ("initialState".equals(qName)) {
            int index = list.add();
            InitialState istate = (InitialState)list.getElement(null, index);
            istate.setData(attributes.getValue("value").trim().split(" "), imanager.normalNodes);
            istate.name = attributes.getValue("name").trim();
        }
        if ("input".equals(qName)) {
            int index = inputs.add();
            InitialState istate = (InitialState)inputs.getElement(null, index);
            istate.setData(attributes.getValue("value").trim().split(" "), imanager.inputNodes);
            istate.name = attributes.getValue("name").trim();
        }
    }
    
    /**
     * @return the list of parameters read by this parser.
     */
	public Object getParameters() {
		return imanager;
	}
}
