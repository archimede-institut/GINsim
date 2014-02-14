package org.ginsim.service.tool.reg2dyn;

import java.util.Collection;

import org.ginsim.core.graph.*;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStatesHandler;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStatesManager;
import org.ginsim.core.utils.data.GenericListListener;
import org.ginsim.core.utils.data.NamedList;
import org.ginsim.service.tool.reg2dyn.priorityclass.PrioritySetDefinition;
import org.ginsim.service.tool.reg2dyn.priorityclass.PrioritySetList;


/**
 * store all simulation parameters and offer a mean to access them.
 * Also deals with updating them when the graph is changed
 */
public class SimulationParameterList extends NamedList<SimulationParameters>
	implements GraphListener<RegulatoryGraph>,  GenericListListener {

    public final RegulatoryGraph graph;
    public final NamedStatesHandler imanager;
    public final PrioritySetList pcmanager;

    /**
     * @param graph
     */
    public SimulationParameterList( Graph graph) {
    	
    	this(graph, null);
    }

    public SimulationParameterList( Graph<RegulatoryNode,RegulatoryMultiEdge> graph, SimulationParameters param) {
    	
        this.graph = (RegulatoryGraph) graph;
        imanager = (NamedStatesHandler) ObjectAssociationManager.getInstance().getObject(graph, NamedStatesManager.KEY, true);
        imanager.getInitialStates().addListListener(this);
        imanager.getInputConfigs().addListListener(this);
        pcmanager = new PrioritySetList(this.graph);
        GraphManager.getInstance().addGraphListener( this.graph, this);
        if (param == null) {
        	add();
        } else {
        	add(param);
        }
    }

    @Override
	public GraphEventCascade graphChanged(RegulatoryGraph g, GraphChangeType type, Object data) {
		switch (type) {
		case NODEADDED:
			nodeAdded(data);
			break;
		case NODEREMOVED:
			nodeRemoved(data);
			break;
		case GRAPHMERGED:
			Collection<?> items = (Collection<?>)data;
			for (Object item: items) {
                if (item instanceof Edge) {

                } else {
                    nodeAdded(item);
                }
			}
			break;

		case NODEUPDATED:
			RegulatoryNode node = (RegulatoryNode)data;
			if (node.isInput()) {
				nodeRemoved(node);
			} else {
				nodeAdded(node);
			}
			break;
		}
		return null;
	}
	
    private void nodeRemoved(Object data) {
        // remove it from priority classes
    	pcmanager.nodeOrder.remove(data);
        for (int i=0 ; i<pcmanager.size() ; i++) {
        	PrioritySetDefinition pcdef = pcmanager.get(i);
    		if (pcdef.m_elt != null) {
    			pcdef.m_elt.remove(data);
    		}
        }
    }
    
	private void nodeAdded(Object data) {
        // if needed, add it to the default priority class!
		RegulatoryNode node = (RegulatoryNode)data;
		if (pcmanager.nodeOrder.contains(node)) {
			return;
		}
		pcmanager.nodeOrder.add(node);
        for (int i=0 ; i<pcmanager.size() ; i++) {
        	PrioritySetDefinition pcdef = pcmanager.get(i);
    		if (pcdef.m_elt != null) {
    			pcdef.m_elt.put(node, pcdef.get(0));
    		}
        }
	}

    public int add(SimulationParameters param, int index) {
    	add(index, param);
    	return indexOf(param);
    }

    public SimulationParameters add() {
        SimulationParameters p = new SimulationParameters(this);
        p.name = findUniqueName("parameter");
        add(p);
        return p;
    }

	public void itemAdded(Object item, int pos) {
	}

	public void itemRemoved(Object item, int pos) {
		
		for (SimulationParameters param: this) {
			if (param.m_initState != null) {
				param.m_initState.remove(item);
			}
		}
	}

	public void contentChanged() {
	}
	public void structureChanged() {
	}

    // FIXME: how to trigger this event now?
	public void list_item_removed( SimulationParameters param) {
		ObjectAssociationManager.getInstance().fireUserUpdate(graph, Reg2DynService.KEY, param.name, null);
	}

}
