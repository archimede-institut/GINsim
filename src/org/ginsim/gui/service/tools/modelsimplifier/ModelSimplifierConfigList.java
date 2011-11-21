package org.ginsim.gui.service.tools.modelsimplifier;

import java.util.Collection;

import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.GraphListener;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.graph.regulatorygraph.RegulatoryVertex;

import fr.univmrs.tagc.GINsim.graph.GsGraphEventCascade;
import fr.univmrs.tagc.common.datastore.SimpleGenericList;

/**
 * store all simplification parameters and offer a mean to access them.
 * Also deals with updating them when the graph is changed
 */
public class ModelSimplifierConfigList extends SimpleGenericList<ModelSimplifierConfig>
	implements GraphListener<RegulatoryVertex, RegulatoryMultiEdge> {

    String s_current;
    RegulatoryGraph graph;

    public ModelSimplifierConfigList( Graph<RegulatoryVertex, RegulatoryMultiEdge> graph) {
    	
        this.graph = (RegulatoryGraph) graph;
    	prefix = "parameter_";
    	canAdd = true;
    	canEdit = true;
    	canRemove = true;
    	canOrder = true;
        graph.addGraphListener(this);
    }

    public GsGraphEventCascade vertexAdded(RegulatoryVertex data) {
        return null;
    }
	public GsGraphEventCascade graphMerged(Collection<RegulatoryVertex> data) {
		return null;
	}
    
    public GsGraphEventCascade vertexRemoved(RegulatoryVertex data) {
    	for (int i=0 ; i<v_data.size() ; i++) {
    		ModelSimplifierConfig cfg = (ModelSimplifierConfig)v_data.get(i);
    		cfg.m_removed.remove(data);
    	}
        return null;
    }

    public GsGraphEventCascade vertexUpdated(RegulatoryVertex data) {
    	return null;
    }

	protected ModelSimplifierConfig doCreate(String name, int pos) {
		ModelSimplifierConfig config = new ModelSimplifierConfig();
		config.setName(name);
		return config;
	}
	public GsGraphEventCascade edgeAdded(RegulatoryMultiEdge data) {
		return null;
	}
	public GsGraphEventCascade edgeRemoved(RegulatoryMultiEdge data) {
		return null;
	}
	public GsGraphEventCascade edgeUpdated(RegulatoryMultiEdge data) {
		return null;
	}
	public void endParsing() {
	}
}
