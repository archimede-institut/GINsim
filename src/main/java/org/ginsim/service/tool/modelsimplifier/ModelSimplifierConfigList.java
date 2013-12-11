package org.ginsim.service.tool.modelsimplifier;

import java.util.*;

import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.GraphChangeType;
import org.ginsim.core.graph.common.GraphEventCascade;
import org.ginsim.core.graph.common.GraphListener;
import org.ginsim.core.graph.objectassociation.UserSupporter;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.utils.data.NamedList;


/**
 * store all simplification parameters and offer a mean to access them.
 * Also deals with updating them when the graph is changed
 */
public class ModelSimplifierConfigList extends NamedList<ModelSimplifierConfig>
	implements GraphListener<RegulatoryGraph>, UserSupporter {

    private String s_current;
    private RegulatoryGraph graph;
    private Set<String> outputStrippers = new HashSet<String>();
    
    public ModelSimplifierConfigList( RegulatoryGraph graph) {
    	
        this.graph = graph;
/*
    	prefix = "parameter_";
    	canAdd = true;
    	canEdit = true;
    	canRemove = true;
    	canOrder = true;
*/
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
	    	for (ModelSimplifierConfig cfg: this) {
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
	
	protected Collection<String> getOutputStrippingUsers() {
		return outputStrippers;
	}

	@Override
	public void update(String oldID, String newID) {
		if (outputStrippers.remove(oldID) && newID != null) {
			outputStrippers.add(newID);
		}
	}

    public List<RegulatoryNode> getNodeOrder() {
        return graph.getNodeOrder();
    }

    public int create() {
        ModelSimplifierConfig cfg = new ModelSimplifierConfig();
        cfg.setName(findUniqueName("Reduction "));

        int pos = size();
        add(cfg);
        return pos;
    }
}
