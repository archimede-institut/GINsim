package org.ginsim.gui.service.tools.modelsimplifier;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.exception.GsException;
import org.ginsim.exception.NotificationMessage;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.regulatorygraph.GsRegulatoryGraph;
import org.ginsim.graph.regulatorygraph.GsRegulatoryVertex;
import org.ginsim.graph.regulatorygraph.RegulatoryGraphFactory;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.GsToolsAction;
import org.ginsim.service.action.modelsimplifier.ModelSimplifierService;
import org.mangosdk.spi.ProviderFor;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.univmrs.tagc.GINsim.graph.GsGraphAssociatedObjectManager;
import fr.univmrs.tagc.common.managerresources.Translator;
import fr.univmrs.tagc.common.xml.XMLHelper;
import fr.univmrs.tagc.common.xml.XMLWriter;

/**
 * main method for the model simplification plugin
 */
@ProviderFor(GsServiceGUI.class)
@GUIFor(ModelSimplifierService.class)
public class ModelSimplifierServiceGUI implements GsServiceGUI {

    static {
        if (!RegulatoryGraphFactory.isObjectManagerRegistred(ModelSimplifierConfigManager.key)) {
            RegulatoryGraphFactory.registerObjectManager(new ModelSimplifierConfigManager());
        }
    }

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof GsRegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new ModelSimplifierAction((GsRegulatoryGraph)graph));
			return actions;
		}
		return null;
	}
}

class ModelSimplifierAction extends GsToolsAction {

	private final GsRegulatoryGraph graph;
	public ModelSimplifierAction(GsRegulatoryGraph graph) {
		super("STR_reduce", "STR_reduce_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (graph.getVertexCount() < 1) {
            graph.addNotificationMessage(new NotificationMessage(graph, 
            		Translator.getString(graph instanceof GsRegulatoryGraph ? "STR_emptyGraph" : "STR_notRegGraph"), 
            		NotificationMessage.NOTIFICATION_WARNING));
            return;
		}

		// TODO: reset edit mode
		// mframe.getActions().setCurrentMode(GsActions.MODE_DEFAULT, 0, false);
		new ModelSimplifierConfigDialog(graph);
	}
	
}

/**
 * Save/open simulation parameters along with the model.
 */
class ModelSimplifierConfigManager implements GsGraphAssociatedObjectManager {

	public static final String key = "modelSimplifier";
	
    public Object doOpen(InputStream is, Graph graph) {
    	
        ModelSimplifierConfigParser parser = new ModelSimplifierConfigParser((GsRegulatoryGraph)graph);
        parser.startParsing(is, false);
        return parser.getParameters();
    }

    public void doSave(OutputStreamWriter os, Graph graph) {
        ModelSimplifierConfigList paramList = (ModelSimplifierConfigList)graph.getObject(key, false);
        List<GsRegulatoryVertex> nodeOrder = ((GsRegulatoryGraph)graph).getNodeOrder();
        if (paramList == null || paramList.getNbElements(null) == 0 || nodeOrder == null || nodeOrder.size() == 0) {
            return;
        }
        try {
            XMLWriter out = new XMLWriter(os, null);
            out.openTag("modelSimplifications");
            // add the available configurations
            for (int i=0 ; i<paramList.getNbElements(null) ; i++) {
                ((ModelSimplifierConfig)paramList.getElement(null, i)).toXML(out, null, 0);
            }
            out.closeTag();
        } catch (IOException e) {
            GUIManager.error(new GsException(GsException.GRAVITY_ERROR, e.getLocalizedMessage()), null);
        }
    }

    public String getObjectName() {
        return key;
    }

    public boolean needSaving( Graph graph) {
        ModelSimplifierConfigList paramList = (ModelSimplifierConfigList)graph.getObject(key, false);
        return paramList != null && paramList.getNbElements(null) > 0;
    }

	public Object doCreate( Graph graph) {
		
		return new ModelSimplifierConfigList( graph);
	}
}

/**
 * parser for simulation parameters file
 */
class ModelSimplifierConfigParser extends XMLHelper {

    public Graph getGraph() {
        // doesn't create a graph!
        return null;
    }
    public String getFallBackDTD() {
        // doesn't use a DTD either
        return null;
    }
    
    List<GsRegulatoryVertex> nodeOrder;
    ModelSimplifierConfigList paramList;
    
    /**
     * @param graph expected node order
     */
    public ModelSimplifierConfigParser(GsRegulatoryGraph graph) {
    	this.nodeOrder = graph.getNodeOrder();
        this.paramList = (ModelSimplifierConfigList)graph.getObject(ModelSimplifierConfigManager.key, true);
    }
    
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        if (qName.equals("simplificationConfig")) {
        	ModelSimplifierConfig cfg = (ModelSimplifierConfig)paramList.getElement(null, paramList.add());
        	cfg.name = attributes.getValue("name");
        	String s_strict = attributes.getValue("strict");
        	if (s_strict != null) {
        		cfg.strict = "true".equals(s_strict);
        	} else {
        		cfg.strict = true;
        	}
        	String[] t_remove = attributes.getValue("removeList").split(" ");
        	for (int i=0 ; i<t_remove.length ; i++) {
        		for (GsRegulatoryVertex vertex: nodeOrder) {
        			if (vertex.getId().equals(t_remove[i])) {
        				cfg.m_removed.put(vertex, null);
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
