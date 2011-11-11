package fr.univmrs.tagc.GINsim.graph;

import java.io.IOException;
import java.io.OutputStreamWriter;

import org.ginsim.exception.GsException;
import org.ginsim.graph.Graph;
import org.ginsim.gui.GUIManager;

import fr.univmrs.tagc.common.xml.XMLWriter;
import fr.univmrs.tagc.common.xml.XMLize;


public abstract class BasicGraphAssociatedManager implements
		GsGraphAssociatedObjectManager {

	protected String key;
	
	public void doSave(OutputStreamWriter os, Graph graph) {
		
        Object o = graph.getObject(key, false);
        if (o != null && o instanceof XMLize) {
	        try {
	            XMLWriter out = new XMLWriter(os, null);
				((XMLize)o).toXML(out, graph, 0);
	        } catch (IOException e) {
	            GUIManager.getInstance().error(new GsException(GsException.GRAVITY_ERROR, e.getLocalizedMessage()), null);
	        }
		}
	}

	public String getObjectName() {
		return key;
	}

	public boolean needSaving( Graph graph) {
		
		return graph.getObject(key, false) != null;
	}

}
