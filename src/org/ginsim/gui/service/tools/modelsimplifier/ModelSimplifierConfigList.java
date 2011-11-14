package org.ginsim.gui.service.tools.modelsimplifier;

import java.util.Collection;

import org.ginsim.graph.common.Graph;

import fr.univmrs.tagc.GINsim.graph.GsGraphEventCascade;
import fr.univmrs.tagc.GINsim.graph.GsGraphListener;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.common.datastore.SimpleGenericList;

/**
 * store all simplification parameters and offer a mean to access them.
 * Also deals with updating them when the graph is changed
 */
public class ModelSimplifierConfigList extends SimpleGenericList<ModelSimplifierConfig>
	implements GsGraphListener<GsRegulatoryVertex, GsRegulatoryMultiEdge> {

    String s_current;
    GsRegulatoryGraph graph;

    public ModelSimplifierConfigList( Graph<GsRegulatoryVertex, GsRegulatoryMultiEdge> graph) {
    	
        this.graph = (GsRegulatoryGraph) graph;
    	prefix = "parameter_";
    	canAdd = true;
    	canEdit = true;
    	canRemove = true;
    	canOrder = true;
        graph.addGraphListener(this);
    }

    public GsGraphEventCascade vertexAdded(GsRegulatoryVertex data) {
        return null;
    }
	public GsGraphEventCascade graphMerged(Collection<GsRegulatoryVertex> data) {
		return null;
	}
    
    public GsGraphEventCascade vertexRemoved(GsRegulatoryVertex data) {
    	for (int i=0 ; i<v_data.size() ; i++) {
    		ModelSimplifierConfig cfg = (ModelSimplifierConfig)v_data.get(i);
    		cfg.m_removed.remove(data);
    	}
        return null;
    }

    public GsGraphEventCascade vertexUpdated(GsRegulatoryVertex data) {
    	return null;
    }

	protected ModelSimplifierConfig doCreate(String name, int pos) {
		ModelSimplifierConfig config = new ModelSimplifierConfig();
		config.setName(name);
		return config;
	}
	public GsGraphEventCascade edgeAdded(GsRegulatoryMultiEdge data) {
		return null;
	}
	public GsGraphEventCascade edgeRemoved(GsRegulatoryMultiEdge data) {
		return null;
	}
	public GsGraphEventCascade edgeUpdated(GsRegulatoryMultiEdge data) {
		return null;
	}
	public void endParsing() {
	}
}
