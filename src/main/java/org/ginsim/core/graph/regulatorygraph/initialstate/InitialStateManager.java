package org.ginsim.core.graph.regulatorygraph.initialstate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import org.ginsim.common.application.GsException;
import org.ginsim.common.xml.XMLHelper;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.objectassociation.BasicGraphAssociatedManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


public class InitialStateManager extends BasicGraphAssociatedManager {

	public static final String KEY = "initialState";
	
	public InitialStateManager() {
		super(KEY, null);
	}
	
    public Object doOpen(InputStream is, Graph graph)  throws GsException{
    	
        initStateParser parser = new initStateParser((RegulatoryGraph)graph);
        parser.startParsing(is, false);
        return parser.getParameters();
    }

    public void doSave(OutputStreamWriter os, Graph graph) throws GsException{
        GsInitialStateList imanager = (GsInitialStateList) getObject(graph);
        try {
            XMLWriter out = new XMLWriter(os, null);
            out.openTag("initialStates");
            imanager.getInitialStates().toXML(out, "initialState");
            imanager.getInputConfigs().toXML(out, "input");
            out.closeTag();
        } catch (IOException e) {
            throw new GsException( "STR_unableToSave", e);
        }
    }

    @Override
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
