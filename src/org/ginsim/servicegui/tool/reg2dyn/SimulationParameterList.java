package org.ginsim.servicegui.tool.reg2dyn;

import java.util.Collection;

import org.ginsim.core.GraphEventCascade;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.common.GraphListener;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.initialstate.GsInitialStateList;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialStateManager;
import org.ginsim.core.graph.regulatorygraph.mutant.MutantListManager;
import org.ginsim.core.graph.regulatorygraph.mutant.RegulatoryMutants;
import org.ginsim.core.utils.data.GenericListListener;
import org.ginsim.core.utils.data.SimpleGenericList;


/**
 * store all simulation parameters and offer a mean to access them.
 * Also deals with updating them when the graph is changed
 */
public class SimulationParameterList extends SimpleGenericList<SimulationParameters> 
	implements GraphListener<RegulatoryNode, RegulatoryMultiEdge>, RegulatoryMutantListener, GenericListListener {

    String s_current;
    RegulatoryGraph graph;
    GsInitialStateList imanager;
    public PriorityClassManager pcmanager;

    /**
     * @param graph
     */
    public SimulationParameterList( Graph graph) {
    	
    	this(graph, null);
    }

    public SimulationParameterList( Graph<RegulatoryNode,RegulatoryMultiEdge> graph, SimulationParameters param) {
    	
        this.graph = (RegulatoryGraph) graph;
        imanager = (GsInitialStateList) ObjectAssociationManager.getInstance().getObject(graph, InitialStateManager.key, true);
        imanager.getInitialStates().addListListener(this);
        imanager.getInputConfigs().addListListener(this);
        pcmanager = new PriorityClassManager(this.graph);
    	prefix = "parameter_";
    	canAdd = true;
    	canEdit = true;
    	canRemove = true;
    	canOrder = true;
        graph.addGraphListener(this);
        RegulatoryMutants mutants = (RegulatoryMutants)  ObjectAssociationManager.getInstance().getObject( graph, MutantListManager.key, true);
        mutants.addListener(this);
        if (param == null) {
        	add();
        } else {
        	add(param,0);
        }
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

    public GraphEventCascade nodeAdded(RegulatoryNode data) {
        // if needed, add it to the default priority class!
        for (int i=0 ; i<pcmanager.getNbElements(null) ; i++) {
        	PriorityClassDefinition pcdef = (PriorityClassDefinition)pcmanager.getElement(null, i);
    		if (pcdef.m_elt != null) {
    			pcdef.m_elt.put((RegulatoryNode)data, pcdef.getElement(null, 0));
    		}
        }
        return null;
    }

	public GraphEventCascade graphMerged(Collection<RegulatoryNode> nodes) {
		for (RegulatoryNode v: nodes) {
			nodeAdded(v);
		}
		return null;
	}
    
    public GraphEventCascade nodeRemoved(RegulatoryNode data) {
        // remove it from priority classes
        for (int i=0 ; i<pcmanager.getNbElements(null) ; i++) {
        	PriorityClassDefinition pcdef = (PriorityClassDefinition)pcmanager.getElement(null, i);
    		if (pcdef.m_elt != null) {
    			pcdef.m_elt.remove(data);
    		}
        }
        return null;
    }

    public GraphEventCascade nodeUpdated(RegulatoryNode data) {
    	return null;
    }

    public int add(SimulationParameters param, int index) {
    	v_data.add(index, param);
    	return v_data.indexOf(param);
    }
    public int add(SimulationParameters param) {
    	return add(param, v_data.size());
    }

    public void mutantAdded(Object mutant) {
    }

    public void mutantRemoved(Object mutant) {
        for (int i=0 ; i< v_data.size() ; i++) {
            SimulationParameters param = (SimulationParameters)v_data.get(i);
            if (param.store.getObject(SimulationParameters.MUTANT) == mutant) {
                param.store.setObject(SimulationParameters.MUTANT, null);
            }
        }
    }

	protected SimulationParameters doCreate(String name, int mode) {
        SimulationParameters parameter = new SimulationParameters(this);
        parameter.name = name;
		return parameter;
	}

	public void itemAdded(Object item, int pos) {
	}

	public void itemRemoved(Object item, int pos) {
		for (int i=0 ; i<v_data.size() ; i++) {
			SimulationParameters param = (SimulationParameters)v_data.get(i);
			if (param.m_initState != null) {
				param.m_initState.remove(item);
			}
		}
	}

	public void contentChanged() {
	}
	public void structureChanged() {
	}
	public void endParsing() {
	}
}
