package org.ginsim.core.graph.objectassociation;

import java.io.IOException;
import java.io.OutputStreamWriter;

import org.ginsim.common.xml.XMLWriter;
import org.ginsim.common.xml.XMLize;
import org.ginsim.core.exception.GsException;
import org.ginsim.core.graph.common.Graph;



public abstract class BasicGraphAssociatedManager implements
		GraphAssociatedObjectManager {

	protected String key;
	
	public void doSave(OutputStreamWriter os, Graph graph) throws GsException{
		
        Object o =  ObjectAssociationManager.getInstance().getObject(graph, key, false);
        if (o != null && o instanceof XMLize) {
        	try{
        		XMLWriter out = new XMLWriter(os, null);
        		((XMLize)o).toXML(out, graph, 0);
        	}
        	catch( IOException ioe){
        		throw new GsException( GsException.GRAVITY_ERROR, ioe);
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
