package org.ginsim.core.graph.objectassociation;

import java.io.IOException;
import java.io.OutputStreamWriter;

import org.ginsim.common.application.GsException;
import org.ginsim.common.xml.XMLWriter;
import org.ginsim.common.xml.XMLize;
import org.ginsim.core.graph.common.Graph;



public abstract class BasicGraphAssociatedManager implements GraphAssociatedObjectManager {

	private final String key;
	private final String[] aliases;
	
	public BasicGraphAssociatedManager(String key, String[] aliases) {
		this.key = key;
		this.aliases = aliases;
	}
	
	public void doSave(OutputStreamWriter os, Graph graph) throws GsException{
		
        Object o =  getObject(graph);
        if (o != null && o instanceof XMLize) {
        	try{
        		XMLWriter out = new XMLWriter(os, null);
        		((XMLize)o).toXML(out);
        	}
        	catch( IOException ioe){
        		throw new GsException( GsException.GRAVITY_ERROR, ioe);
        	}
		} 
	}

	public String getObjectName() {
		return key;
	}

	public String[] getAliases() {
		return aliases;
	}

	public boolean needSaving( Graph graph) {
		return getObject(graph) != null;
	}

	public Object getObject( Graph graph) {
		return ObjectAssociationManager.getInstance().getObject(graph, key, false);
	}

    @Override
    public boolean handles(String key) {
        if (key == null) {
            return false;
        }

        if (key.equals(this.key)) {
            return true;
        }

        if (aliases != null) {
            for (String alias: aliases) {
                if (alias.equals(key)) {
                    return true;
                }
            }
        }

        return false;
    }
}
