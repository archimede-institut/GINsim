package org.ginsim.service.tool.modelreduction;

import java.util.*;

import org.ginsim.core.graph.GSGraphManager;
import org.ginsim.core.graph.GraphChangeType;
import org.ginsim.core.graph.GraphEventCascade;
import org.ginsim.core.graph.GraphListener;
import org.ginsim.core.graph.objectassociation.UserSupporter;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.utils.data.NamedList;
import org.ginsim.service.tool.modelreduction.ReductionConfig;

/**
 * store all simplification parameters and offer a mean to access them.
 * Also deals with updating them when the graph is changed
 */
public class ListOfReductionConfigs extends NamedList<ReductionConfig>
	implements GraphListener<RegulatoryGraph>, UserSupporter {

    private String s_current;
    private RegulatoryGraph graph;
    private Set<String> outputStrippers = new HashSet<String>();
    private Set<String> fixedPropagaters = new HashSet<String>();
    private Map<String, ReductionConfig> users = new HashMap<String, ReductionConfig>();
    
    public ListOfReductionConfigs(RegulatoryGraph graph) {
    	
        this.graph = graph;
/*
    	prefix = "parameter_";
    	canAdd = true;
    	canEdit = true;
    	canRemove = true;
    	canOrder = true;
*/
        GSGraphManager.getInstance().addGraphListener( this.graph, this);
    }

	protected ReductionConfig doCreate(String name, int pos) {
		ReductionConfig config = new ReductionConfig();
		config.setName(name);
		return config;
	}

	@Override
	public GraphEventCascade graphChanged(RegulatoryGraph g,
			GraphChangeType type, Object data) {
		if (type == GraphChangeType.NODEREMOVED) {
	    	for (ReductionConfig cfg: this) {
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

    public void setPropagateFixed(String key, boolean use) {
        if (!use) {
            fixedPropagaters.remove(key);
        } else {
            fixedPropagaters.add(key);
        }
    }

    public boolean isStrippingOutput(String key) {
        return outputStrippers.contains(key);
    }

    public boolean isPropagatingFixed(String key) {
        return fixedPropagaters.contains(key);
    }

    public void useReduction(String key, ReductionConfig reduction) {
        if (reduction == null) {
            //outputStrippers.remove(key);
            //fixedPropagaters.remove(key);
            users.remove(key);

        } else {
            users.put(key, reduction);
            // decouplage reduction output do do
            if(reduction.outputs)   {outputStrippers.add(key);}
        }
    }

    public ReductionConfig getUsedReduction(String key) {
        return users.get(key);
    }


    public Collection<String> getOutputStrippingUsers() {
        return outputStrippers;
    }

    public Collection<String> getFixedPropagationUsers() {
        return fixedPropagaters;
    }

	@Override
	public void update(String oldID, String newID) {
        if (outputStrippers.remove(oldID) && newID != null) {
            outputStrippers.add(newID);
        }
        if (fixedPropagaters.remove(oldID) && newID != null) {
            fixedPropagaters.add(newID);
        }
	}

    public List<RegulatoryNode> getNodeOrder() {
        return graph.getNodeOrder();
    }

    public int create() {
        ReductionConfig cfg = new ReductionConfig();
        cfg.setName(findUniqueName("Reduction "));

        int pos = size();
        add(cfg);
        return pos;
    }
}
