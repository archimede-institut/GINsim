package org.ginsim.core.graph.regulatorygraph.namedstates;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;

import org.ginsim.common.application.GsException;
import org.ginsim.common.xml.XMLHelper;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.objectassociation.BasicGraphAssociatedManager;
import org.ginsim.core.graph.objectassociation.GraphAssociatedObjectManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.mangosdk.spi.ProviderFor;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Manager for the named states associated data.
 *
 * @author Aurelien Naldi
 */
@ProviderFor(GraphAssociatedObjectManager.class)
public class NamedStatesManager extends BasicGraphAssociatedManager<NamedStatesHandler> {

	public static final String KEY = "initialState";
	
	public NamedStatesManager() {
		super(KEY, null, RegulatoryGraph.class);
	}
	
    public NamedStatesHandler doOpen(InputStream is, Graph graph)  throws GsException{
    	
        initStateParser parser = new initStateParser((RegulatoryGraph)graph);
        parser.startParsing(is, false);
        return parser.getParameters();
    }

    public void doSave(OutputStreamWriter os, Graph graph) throws GsException{
        NamedStatesHandler imanager = (NamedStatesHandler) getObject(graph);
        try {
            XMLWriter out = new XMLWriter(os);
            out.openTag("initialStates");
            imanager.getInitialStates().toXML(out, "initialState");
            imanager.getInputConfigs().toXML(out, "input");
            out.closeTag();
        } catch (IOException e) {
            throw new GsException( "STR_unableToSave", e);
        }
    }

    @Override
	public NamedStatesHandler doCreate( Graph graph) {
		return new NamedStatesHandler(graph);
	}
}


class initStateParser extends XMLHelper {

    NamedStatesHandler imanager;
    NamedStateList list, inputs;
    
    /**
     * @param graph expected node order
     */
    public initStateParser(RegulatoryGraph graph) {
        imanager = new NamedStatesHandler( graph);
        list = imanager.getInitialStates();
        inputs = imanager.getInputConfigs();
    }

    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        
        if ("initialState".equals(qName)) {
            int index = list.add();
            NamedState istate = list.get(index);
            istate.setData(attributes.getValue("value").trim().split(" "), imanager.normalNodes);
            istate.name = attributes.getValue("name").trim();
        }
        if ("input".equals(qName)) {
            int index = inputs.add();
            NamedState istate = inputs.get(index);
            istate.setData(attributes.getValue("value").trim().split(" "), imanager.inputNodes);
            istate.name = attributes.getValue("name").trim();
        }
    }
    
    /**
     * @return the list of named states read by this parser.
     */
	public NamedStatesHandler getParameters() {
		return imanager;
	}
}
