package org.ginsim.gui.service.tool.modelsimplifier;

import java.util.Collection;

import org.ginsim.core.GraphEventCascade;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.GraphListener;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.graph.regulatorygraph.RegulatoryNode;

import fr.univmrs.tagc.common.datastore.SimpleGenericList;

/**
 * store all simplification parameters and offer a mean to access them.
 * Also deals with updating them when the graph is changed
 */
public class ModelSimplifierConfigList extends SimpleGenericList<ModelSimplifierConfig>
	implements GraphListener<RegulatoryNode, RegulatoryMultiEdge> {

    String s_current;
    RegulatoryGraph graph;

    public ModelSimplifierConfigList( Graph<RegulatoryNode, RegulatoryMultiEdge> graph) {
    	
        this.graph = (RegulatoryGraph) graph;
    	prefix = "parameter_";
    	canAdd = true;
    	canEdit = true;
    	canRemove = true;
    	canOrder = true;
        graph.addGraphListener(this);
    }

    public GraphEventCascade nodeAdded(RegulatoryNode data) {
        return null;
    }
	public GraphEventCascade graphMerged(Collection<RegulatoryNode> data) {
		return null;
	}
    
    public GraphEventCascade nodeRemoved(RegulatoryNode data) {
    	for (int i=0 ; i<v_data.size() ; i++) {
    		ModelSimplifierConfig cfg = (ModelSimplifierConfig)v_data.get(i);
    		cfg.m_removed.remove(data);
    	}
        return null;
    }

    public GraphEventCascade nodeUpdated(RegulatoryNode data) {
    	return null;
    }

	protected ModelSimplifierConfig doCreate(String name, int pos) {
		ModelSimplifierConfig config = new ModelSimplifierConfig();
		config.setName(name);
		return config;
	}
	public GraphEventCascade edgeAdded(RegulatoryMultiEdge data) {
		return null;
	}
	public GraphEventCascade edgeRemoved(RegulatoryMultiEdge data) {
		return null;
	}
	public GraphEventCascade edgeUpdated(RegulatoryMultiEdge data) {
		return null;
	}
	public void endParsing() {
	}
}
