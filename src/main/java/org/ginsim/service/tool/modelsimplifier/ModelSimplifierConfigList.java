package org.ginsim.service.tool.modelsimplifier;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.GraphChangeType;
import org.ginsim.core.graph.common.GraphEventCascade;
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

    private String s_current;
    private RegulatoryGraph graph;
    private Set<String> outputStrippers = new HashSet<String>();
    
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
	
	public void setStrippingOutput(String key, boolean use) {
		if (!use) {
			outputStrippers.remove(key);
		} else {
			outputStrippers.add(key);
		}
	}
	
	public boolean isStrippingOutput(String key) {
		return outputStrippers.contains(key);
	}
}
