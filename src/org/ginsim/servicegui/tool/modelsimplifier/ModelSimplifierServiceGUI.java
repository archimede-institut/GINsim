package org.ginsim.servicegui.tool.modelsimplifier;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.common.xml.XMLHelper;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.exception.GsException;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.objectassociation.GraphAssociatedObjectManager;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.notification.NotificationManager;
import org.ginsim.gui.GUIManager;
import org.ginsim.service.tool.modelsimplifier.ModelSimplifierService;
import org.ginsim.servicegui.ServiceGUI;
import org.ginsim.servicegui.common.GUIFor;
import org.ginsim.servicegui.common.ToolAction;
import org.mangosdk.spi.ProviderFor;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;


/**
 * main method for the model simplification plugin
 */
@ProviderFor(ServiceGUI.class)
@GUIFor(ModelSimplifierService.class)
public class ModelSimplifierServiceGUI implements ServiceGUI {

    static {
    	if( !ObjectAssociationManager.getInstance().isObjectManagerRegistred( RegulatoryGraph.class, ModelSimplifierConfigManager.key)){
    		ObjectAssociationManager.getInstance().registerObjectManager( RegulatoryGraph.class, new ModelSimplifierConfigManager());
        }
    }

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof RegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new ModelSimplifierAction((RegulatoryGraph)graph));
			return actions;
		}
		return null;
	}
}

class ModelSimplifierAction extends ToolAction {

	private final RegulatoryGraph graph;
	public ModelSimplifierAction(RegulatoryGraph graph) {
		super("STR_reduce", "STR_reduce_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (graph.getNodeCount() < 1) {
            NotificationManager.publishWarning( graph, graph instanceof RegulatoryGraph ? "STR_emptyGraph" : "STR_notRegGraph");
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
class ModelSimplifierConfigManager implements GraphAssociatedObjectManager {

	public static final String key = "modelSimplifier";
	
    public Object doOpen(InputStream is, Graph graph) {
    	
        ModelSimplifierConfigParser parser = new ModelSimplifierConfigParser((RegulatoryGraph)graph);
        parser.startParsing(is, false);
        return parser.getParameters();
    }

    public void doSave(OutputStreamWriter os, Graph graph) {
        ModelSimplifierConfigList paramList = (ModelSimplifierConfigList) ObjectAssociationManager.getInstance().getObject( graph, key, false);
        List<RegulatoryNode> nodeOrder = ((RegulatoryGraph)graph).getNodeOrder();
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
            new GsException( "STR_unableToSave", e);
        }
    }

    public String getObjectName() {
        return key;
    }

    public boolean needSaving( Graph graph) {
        ModelSimplifierConfigList paramList = (ModelSimplifierConfigList) ObjectAssociationManager.getInstance().getObject( graph, key, false);
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
    
    List<RegulatoryNode> nodeOrder;
    ModelSimplifierConfigList paramList;
    
    /**
     * @param graph expected node order
     */
    public ModelSimplifierConfigParser(RegulatoryGraph graph) {
    	this.nodeOrder = graph.getNodeOrder();
        this.paramList = (ModelSimplifierConfigList) ObjectAssociationManager.getInstance().getObject( graph, ModelSimplifierConfigManager.key, true);
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
        		for (RegulatoryNode vertex: nodeOrder) {
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
