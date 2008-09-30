package fr.univmrs.tagc.GINsim.regulatoryGraph.modelModifier;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.GINsim.graph.GsActionProvider;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphAssociatedObjectManager;
import fr.univmrs.tagc.GINsim.graph.GsGraphNotificationMessage;
import fr.univmrs.tagc.GINsim.gui.GsActions;
import fr.univmrs.tagc.GINsim.gui.GsMainFrame;
import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.tagc.GINsim.plugin.GsPlugin;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraphDescriptor;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.common.GsException;
import fr.univmrs.tagc.common.manageressources.Translator;
import fr.univmrs.tagc.common.xml.XMLHelper;
import fr.univmrs.tagc.common.xml.XMLWriter;

/**
 * main method for the model simplification plugin
 */
public class ModelSimplifierPlugin implements GsPlugin, GsActionProvider {

    static {
        if (!GsRegulatoryGraphDescriptor.isObjectManagerRegistred(ModelSimplifierConfigManager.key)) {
            GsRegulatoryGraphDescriptor.registerObjectManager(new ModelSimplifierConfigManager());
        }
    }
    private GsPluggableActionDescriptor[] t_action = null;

    public void registerPlugin() {
        GsRegulatoryGraphDescriptor.registerActionProvider(this);
    }

    public GsPluggableActionDescriptor[] getT_action(int actionType,
            GsGraph graph) {
        if (actionType != ACTION_ACTION) {
            return null;
        }
        if (t_action == null) {
            t_action = new GsPluggableActionDescriptor[1];
            t_action[0] = new GsPluggableActionDescriptor("STR_reduce",
                    "STR_reduce_descr", null, this, ACTION_ACTION, 0);
        }
        return t_action;
    }

    public void runAction(int actionType, int ref, GsGraph graph, JFrame frame) throws GsException {
        if (actionType != ACTION_ACTION) {
            return;
        }
        if (!(graph instanceof GsRegulatoryGraph) || graph.getNodeOrder().size() < 1) {
            graph.addNotificationMessage(new GsGraphNotificationMessage(graph, 
            		Translator.getString(graph instanceof GsRegulatoryGraph ? "STR_emptyGraph" : "STR_notRegGraph"), 
            		GsGraphNotificationMessage.NOTIFICATION_WARNING));
            return;
        }
		if (ref == 0) {
			GsMainFrame mframe = graph.getGraphManager().getMainFrame();
			if (mframe != null) {
				mframe.getGsAction().setCurrentMode(GsActions.MODE_DEFAULT, 0, false);
				new ModelSimplifierConfigDialog((GsRegulatoryGraph)graph);
			}
		}
	}
}


/**
 * Save/open simulation parameters along with the model.
 */
class ModelSimplifierConfigManager implements GsGraphAssociatedObjectManager {

	public static final String key = "modelSimplifier";
	
    public Object doOpen(InputStream is, GsGraph graph) {
        ModelSimplifierConfigParser parser = new ModelSimplifierConfigParser((GsRegulatoryGraph)graph);
        parser.startParsing(is, false);
        return parser.getParameters();
    }

    public void doSave(OutputStreamWriter os, GsGraph graph) {
        ModelSimplifierConfigList paramList = (ModelSimplifierConfigList)graph.getObject(key, false);
        List nodeOrder = graph.getNodeOrder();
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
            GsEnv.error(new GsException(GsException.GRAVITY_ERROR, e.getLocalizedMessage()), null);
        }
    }

    public String getObjectName() {
        return key;
    }

    public boolean needSaving(GsGraph graph) {
        ModelSimplifierConfigList paramList = (ModelSimplifierConfigList)graph.getObject(key, false);
        return paramList != null && paramList.getNbElements(null) > 0;
    }

	public Object doCreate(GsGraph graph) {
		return new ModelSimplifierConfigList(graph);
	}
}

/**
 * parser for simulation parameters file
 */
class ModelSimplifierConfigParser extends XMLHelper {

    public GsGraph getGraph() {
        // doesn't create a graph!
        return null;
    }
    public String getFallBackDTD() {
        // doesn't use a DTD either
        return null;
    }
    
    List nodeOrder;
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
        		Iterator it = nodeOrder.iterator();
        		while (it.hasNext()) {
        			GsRegulatoryVertex vertex = (GsRegulatoryVertex)it.next();
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
