package org.ginsim.gui.service.tools.reg2dyn;

import java.util.Collection;

import org.ginsim.graph.Graph;

import fr.univmrs.tagc.GINsim.graph.GsGraphEventCascade;
import fr.univmrs.tagc.GINsim.graph.GsGraphListener;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsMutantListManager;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialStateList;
import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialStateManager;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.GsRegulatoryMutants;
import fr.univmrs.tagc.common.datastore.GenericListListener;
import fr.univmrs.tagc.common.datastore.SimpleGenericList;

/**
 * store all simulation parameters and offer a mean to access them.
 * Also deals with updating them when the graph is changed
 */
public class GsSimulationParameterList extends SimpleGenericList<GsSimulationParameters> 
	implements GsGraphListener<GsRegulatoryVertex, GsRegulatoryMultiEdge>, GsRegulatoryMutantListener, GenericListListener {

    String s_current;
    GsRegulatoryGraph graph;
    GsInitialStateList imanager;
    public PriorityClassManager pcmanager;

    /**
     * @param graph
     */
    public GsSimulationParameterList( Graph graph) {
    	
    	this(graph, null);
    }

    public GsSimulationParameterList( Graph<GsRegulatoryVertex,GsRegulatoryMultiEdge> graph, GsSimulationParameters param) {
    	
        this.graph = (GsRegulatoryGraph) graph;
        imanager = (GsInitialStateList) graph.getObject( GsInitialStateManager.key, true);
        imanager.getInitialStates().addListListener(this);
        imanager.getInputConfigs().addListListener(this);
        pcmanager = new PriorityClassManager(this.graph);
    	prefix = "parameter_";
    	canAdd = true;
    	canEdit = true;
    	canRemove = true;
    	canOrder = true;
        graph.addGraphListener(this);
        GsRegulatoryMutants mutants = (GsRegulatoryMutants) graph.getObject(GsMutantListManager.key, true);
        mutants.addListener(this);
        if (param == null) {
        	add();
        } else {
        	add(param,0);
        }
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

    public GsGraphEventCascade vertexAdded(GsRegulatoryVertex data) {
        // if needed, add it to the default priority class!
        for (int i=0 ; i<pcmanager.getNbElements(null) ; i++) {
        	PriorityClassDefinition pcdef = (PriorityClassDefinition)pcmanager.getElement(null, i);
    		if (pcdef.m_elt != null) {
    			pcdef.m_elt.put((GsRegulatoryVertex)data, pcdef.getElement(null, 0));
    		}
        }
        return null;
    }

	public GsGraphEventCascade graphMerged(Collection<GsRegulatoryVertex> nodes) {
		for (GsRegulatoryVertex v: nodes) {
			vertexAdded(v);
		}
		return null;
	}
    
    public GsGraphEventCascade vertexRemoved(GsRegulatoryVertex data) {
        // remove it from priority classes
        for (int i=0 ; i<pcmanager.getNbElements(null) ; i++) {
        	PriorityClassDefinition pcdef = (PriorityClassDefinition)pcmanager.getElement(null, i);
    		if (pcdef.m_elt != null) {
    			pcdef.m_elt.remove(data);
    		}
        }
        return null;
    }

    public GsGraphEventCascade vertexUpdated(GsRegulatoryVertex data) {
    	return null;
    }

    public int add(GsSimulationParameters param, int index) {
    	v_data.add(index, param);
    	return v_data.indexOf(param);
    }
    public int add(GsSimulationParameters param) {
    	return add(param, v_data.size());
    }

    public void mutantAdded(Object mutant) {
    }

    public void mutantRemoved(Object mutant) {
        for (int i=0 ; i< v_data.size() ; i++) {
            GsSimulationParameters param = (GsSimulationParameters)v_data.get(i);
            if (param.store.getObject(GsSimulationParameters.MUTANT) == mutant) {
                param.store.setObject(GsSimulationParameters.MUTANT, null);
            }
        }
    }

	protected GsSimulationParameters doCreate(String name, int mode) {
        GsSimulationParameters parameter = new GsSimulationParameters(this);
        parameter.name = name;
		return parameter;
	}

	public void itemAdded(Object item, int pos) {
	}

	public void itemRemoved(Object item, int pos) {
		for (int i=0 ; i<v_data.size() ; i++) {
			GsSimulationParameters param = (GsSimulationParameters)v_data.get(i);
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
