package fr.univmrs.tagc.GINsim.regulatoryGraph.initialState;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphAssociatedObjectManager;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.xml.XMLHelper;
import fr.univmrs.tagc.common.xml.XMLWriter;

public class GsInitialStateManager implements GsGraphAssociatedObjectManager {

	public static final String key = "initialState";
	
    public Object doOpen(InputStream is, GsGraph graph) {
        initStateParser parser = new initStateParser((GsRegulatoryGraph)graph);
        parser.startParsing(is, false);
        return parser.getParameters();
    }

    public void doSave(OutputStreamWriter os, GsGraph graph) {
        GsInitialStateList list = (GsInitialStateList)graph.getObject(key, true);
        List nodeOrder = graph.getNodeOrder();
        if (list == null || list.getNbElements(null) == 0 || nodeOrder == null || nodeOrder.size() == 0) {
            return;
        }
        try {
            XMLWriter out = new XMLWriter(os, null);
            out.openTag("initialStates");
            for (int i=0 ; i<list.getNbElements(null) ; i++) {
            	GsInitialState is = (GsInitialState)list.getElement(null, i);
            	out.openTag("initialState");
            	out.addAttr("name", is.name);
                String s = "";
                Iterator it_line = is.m.keySet().iterator();
                while (it_line.hasNext()) {
                        GsRegulatoryVertex vertex = (GsRegulatoryVertex)it_line.next();
                        List v_val = (List)is.m.get(vertex);
                        s += vertex.getId();
                        for (int j=0 ; j<v_val.size() ; j++) {
                                s += ";"+((Integer)v_val.get(j)).intValue();
                        }
                        s += " ";
                }
            	out.addAttr("value", s.trim());
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
        GsInitialStateList list = (GsInitialStateList)graph.getObject(key, false);
        return list != null && list.getNbElements(null) > 0;
    }

	public Object doCreate(GsGraph graph) {
		GsInitialStateList l = new GsInitialStateList(graph);
		return l;
	}
}


class initStateParser extends XMLHelper {

	List nodeOrder;
    GsInitialStateList list;
    
    /**
     * @param graph expected node order
     */
    public initStateParser(GsRegulatoryGraph graph) {
        this.nodeOrder = graph.getNodeOrder();
        list = new GsInitialStateList(graph);
    }
    
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        
        if ("initialState".equals(qName)) {
        	int index = list.add();
        	GsInitialState istate = (GsInitialState)list.getElement(null, index);
        	istate.setData(attributes.getValue("value").trim().split(" "), nodeOrder);
        	istate.name = attributes.getValue("name").trim();
        }
    }
    
    /**
     * @return the list of parameters read by this parser.
     */
	public Object getParameters() {
		return list;
	}
    public GsGraph getGraph() {
        // doesn't create a graph!
        return null;
    }
    public String getFallBackDTD() {
        // doesn't use a DTD either
        return null;
    }
}
