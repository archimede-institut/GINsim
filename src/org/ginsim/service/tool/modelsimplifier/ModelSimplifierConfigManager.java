package org.ginsim.service.tool.modelsimplifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import org.ginsim.common.exception.GsException;
import org.ginsim.common.xml.XMLHelper;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.objectassociation.BasicGraphAssociatedManager;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Save/open simulation parameters along with the model.
 */
public class ModelSimplifierConfigManager extends BasicGraphAssociatedManager {

	public static final String KEY = "modelSimplifier";
	
	public ModelSimplifierConfigManager() {
		super(KEY, null);
	}
	
    public Object doOpen(InputStream is, Graph graph) throws GsException {
    	
        ModelSimplifierConfigParser parser = new ModelSimplifierConfigParser((RegulatoryGraph)graph);
        parser.startParsing(is, false);
        return parser.getParameters();
    }

    public void doSave(OutputStreamWriter os, Graph graph) {
        ModelSimplifierConfigList paramList = (ModelSimplifierConfigList) getObject(graph);
        List<RegulatoryNode> nodeOrder = ((RegulatoryGraph)graph).getNodeOrder();
        try {
            XMLWriter out = new XMLWriter(os, null);
            out.openTag("modelSimplifications");
            // add the available configurations
            for (int i=0 ; i<paramList.getNbElements(null) ; i++) {
                ((ModelSimplifierConfig)paramList.getElement(null, i)).toXML(out, null, 0);
            }
            out.closeTag();
        } catch (IOException e) {
            new GsException( "STR_unableToSave", e);
        }
    }

	public Object doCreate( Graph graph) {
		return new ModelSimplifierConfigList( graph);
	}
}

/**
 * parser for simulation parameters file
 */
class ModelSimplifierConfigParser extends XMLHelper {

    List<RegulatoryNode> nodeOrder;
    ModelSimplifierConfigList paramList;
    
    /**
     * @param graph expected node order
     */
    public ModelSimplifierConfigParser(RegulatoryGraph graph) {
    	this.nodeOrder = graph.getNodeOrder();
        this.paramList = (ModelSimplifierConfigList) ObjectAssociationManager.getInstance().getObject( graph, ModelSimplifierConfigManager.KEY, true);
    }
    
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (qName.equals("simplificationConfig")) {
        	ModelSimplifierConfig cfg = (ModelSimplifierConfig)paramList.getElement(null, paramList.add());
        	cfg.setName(attributes.getValue("name"));
        	String s_strict = attributes.getValue("strict");
        	if (s_strict != null) {
        		cfg.strict = "true".equals(s_strict);
        	} else {
        		cfg.strict = true;
        	}
        	String[] t_remove = attributes.getValue("removeList").split(" ");
        	for (int i=0 ; i<t_remove.length ; i++) {
        		for (RegulatoryNode vertex: nodeOrder) {
        			if (vertex.getId().equals(t_remove[i])) {
        				cfg.remove(vertex);
        			}
        		}
        	}
        }
    }
    
    public void endElement (String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
    }

    /**
     * @return the list of parameters read by this parser.
     */
	public Object getParameters() {
		return paramList;
	}
}