package org.ginsim.graph.objectassociation;

import java.io.IOException;
import java.io.OutputStreamWriter;

import org.ginsim.common.xml.XMLWriter;
import org.ginsim.common.xml.XMLize;
import org.ginsim.exception.GsException;
import org.ginsim.graph.common.Graph;
import org.ginsim.gui.GUIManager;



public abstract class BasicGraphAssociatedManager implements
		GraphAssociatedObjectManager {

	protected String key;
	
	public void doSave(OutputStreamWriter os, Graph graph) {
		
        Object o =  ObjectAssociationManager.getInstance().getObject(graph, key, false);
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
		
		return ObjectAssociationManager.getInstance().getObject(graph, key, false) != null;
	}

}
