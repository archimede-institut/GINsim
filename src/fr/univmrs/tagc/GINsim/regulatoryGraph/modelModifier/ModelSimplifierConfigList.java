package fr.univmrs.tagc.GINsim.regulatoryGraph.modelModifier;

import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.graph.GsGraphEventCascade;
import fr.univmrs.tagc.GINsim.graph.GsGraphListener;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.common.datastore.GenericListListener;
import fr.univmrs.tagc.common.datastore.SimpleGenericList;

/**
 * store all simulation parameters and offer a mean to access them.
 * Also deals with updating them when the graph is changed
 */
public class ModelSimplifierConfigList extends SimpleGenericList 
	implements GsGraphListener, GenericListListener {

    String s_current;
    GsRegulatoryGraph graph;

    public ModelSimplifierConfigList(GsGraph graph) {
        this.graph = (GsRegulatoryGraph)graph;
    	prefix = "parameter_";
    	canAdd = true;
    	canEdit = true;
    	canRemove = true;
    	canOrder = true;
        graph.addGraphListener(this);
    }


    public GsGraphEventCascade vertexAdded(Object data) {
        return null;
    }

	public GsGraphEventCascade graphMerged(Object data) {
		return null;
	}
    
    public GsGraphEventCascade vertexRemoved(Object data) {
    	for (int i=0 ; i<v_data.size() ; i++) {
    		ModelSimplifierConfig cfg = (ModelSimplifierConfig)v_data.get(i);
    		cfg.m_removed.remove(data);
    	}
        return null;
    }

    public GsGraphEventCascade vertexUpdated(Object data) {
    	return null;
    }

	protected Object doCreate(String name) {
		ModelSimplifierConfig config = new ModelSimplifierConfig();
		config.setName(name);
		return config;
	}

	public void itemAdded(Object item, int pos) {
	}

	public void itemRemoved(Object item, int pos) {
	}

	public void contentChanged() {
	}
	public void structureChanged() {
	}


	public GsGraphEventCascade edgeAdded(Object data) {
		return null;
	}


	public GsGraphEventCascade edgeRemoved(Object data) {
		return null;
	}


	public GsGraphEventCascade edgeUpdated(Object data) {
		return null;
	}


	public void endParsing() {
	}
}
