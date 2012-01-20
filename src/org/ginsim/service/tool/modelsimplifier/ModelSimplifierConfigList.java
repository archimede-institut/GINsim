package org.ginsim.service.tool.modelsimplifier;

import java.util.Collection;

import org.ginsim.core.GraphEventCascade;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.GraphChangeType;
import org.ginsim.core.graph.common.GraphListener;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.utils.data.SimpleGenericList;


/**
 * store all simplification parameters and offer a mean to access them.
 * Also deals with updating them when the graph is changed
 */
public class ModelSimplifierConfigList extends SimpleGenericList<ModelSimplifierConfig>
	implements GraphListener<RegulatoryGraph> {

    String s_current;
    RegulatoryGraph graph;

    public ModelSimplifierConfigList( Graph<RegulatoryNode, RegulatoryMultiEdge> graph) {
    	
        this.graph = (RegulatoryGraph) graph;
    	prefix = "parameter_";
    	canAdd = true;
    	canEdit = true;
    	canRemove = true;
    	canOrder = true;
        GraphManager.getInstance().addGraphListener( this.graph, this);
    }

	protected ModelSimplifierConfig doCreate(String name, int pos) {
		ModelSimplifierConfig config = new ModelSimplifierConfig();
		config.setName(name);
		return config;
	}

	@Override
	public GraphEventCascade graphChanged(RegulatoryGraph g,
			GraphChangeType type, Object data) {
		if (type == GraphChangeType.NODEREMOVED) {
	    	for (int i=0 ; i<v_data.size() ; i++) {
	    		ModelSimplifierConfig cfg = (ModelSimplifierConfig)v_data.get(i);
	    		cfg.m_removed.remove(data);
	    	}
		}
        return null;
	}
}
