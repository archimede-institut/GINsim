package fr.univmrs.ibdm.GINsim.reg2dyn;

import java.util.Vector;

import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsGraphEventCascade;
import fr.univmrs.ibdm.GINsim.graph.GsGraphListener;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsMutantListManager;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.initialState.GsInitialStateList;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.initialState.GsInitialStateManager;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.mutant.GsRegulatoryMutants;
import fr.univmrs.tagc.datastore.GenericListListener;
import fr.univmrs.tagc.datastore.SimpleGenericList;

/**
 * store all simulation parameters and offer a mean to access them.
 * Also deals with updating them when the graph is changed
 */
public class GsSimulationParameterList extends SimpleGenericList 
	implements GsGraphListener, GsRegulatoryMutantListener, GenericListListener {

    String s_current;
    GsRegulatoryGraph graph;
    GsInitialStateList imanager;

    /**
     * @param graph
     */
    public GsSimulationParameterList(GsGraph graph) {
    	this(graph, null);
    }

    public GsSimulationParameterList(GsGraph graph, GsSimulationParameters param) {
        this.graph = (GsRegulatoryGraph)graph;
        imanager = (GsInitialStateList)graph.getObject(GsInitialStateManager.key, true);
        imanager.addListListener(this);
    	prefix = "parameter_";
    	canAdd = true;
    	canEdit = true;
    	canRemove = true;
    	canOrder = true;
        graph.addGraphListener(this);
        GsRegulatoryMutants mutants = (GsRegulatoryMutants)graph.getObject(GsMutantListManager.key, true);
        mutants.addListener(this);
        if (param == null) {
        	add(0, 0);
        } else {
        	add(param,0);
        }
    }

    public GsGraphEventCascade edgeAdded(Object data) {
        return null;
    }

    public GsGraphEventCascade edgeRemoved(Object data) {
        return null;
    }

    public GsGraphEventCascade edgeUpdated(Object data) {
        return null;
    }

    public GsGraphEventCascade vertexAdded(Object data) {
        // if needed, add it to the default priority class!
        for (int i=0 ; i<v_data.size() ; i++) {
            GsSimulationParameters param = (GsSimulationParameters)v_data.get(i);
            if (param.m_elt != null) {
            	param.m_elt.put(data, param.v_class.get(0));
            }
        }
        return null;
    }

	public GsGraphEventCascade graphMerged(Object data) {
		Vector v = (Vector)data;
		for (int i=0 ; i<v.size() ; i++) {
			vertexAdded(v.get(i));
		}
		return null;
	}
    
    public GsGraphEventCascade vertexRemoved(Object data) {
        // remove it from priority classes and initial states
        for (int i=0 ; i<v_data.size() ; i++) {
            GsSimulationParameters param = (GsSimulationParameters)v_data.get(i);
            if (param.m_elt != null) {
                param.m_elt.remove(data);
            }
        }
        return null;
    }

    public GsGraphEventCascade vertexUpdated(Object data) {
    	return null;
    }

    public int copy(int index) {
    	if (index < 0 || index >= v_data.size()) {
    		return -1;
    	}
    	GsSimulationParameters old = (GsSimulationParameters)v_data.get(index);
        // find an unused name
        String s = null;
        boolean[] t = new boolean[getNbElements()];
        for (int j=0 ; j<t.length ; j++) {
            t[j] = true;
        }
        for (int j=0 ; j<t.length ; j++) {
            GsSimulationParameters param = (GsSimulationParameters)v_data.get(j);
            if (param.name.startsWith(old.name+"_")) {
                try {
                    int v = Integer.parseInt(param.name.substring(10));
                    if (v > 0 && v <= t.length) {
                        t[v-1] = false;
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
        for (int j=0 ; j<t.length ; j++) {
            if (t[j]) {
                s = old.name+"_"+(j+1);
                break;
            }
        }
        if (s == null) {
            s = old.name+"_"+(t.length+1);
        }

        GsSimulationParameters parameter = (GsSimulationParameters)old.clone();
        parameter.name = s;
        index++;
        if (index<=0 || index>v_data.size()) {
            index = v_data.size();
        }
    	return add(parameter, index);
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
            if (param.mutant == mutant) {
                param.mutant = null;
            }
        }
    }

	protected Object doCreate(String name, int type) {
        GsSimulationParameters parameter = new GsSimulationParameters(graph.getNodeOrder());
        parameter.name = name;
		return parameter;
	}

	public void ItemAdded(Object item, int pos) {
	}

	public void itemRemoved(Object item, int pos) {
		for (int i=0 ; i<v_data.size() ; i++) {
			GsSimulationParameters param = (GsSimulationParameters)v_data.get(i);
			if (param.m_initState != null) {
				param.m_initState.remove(item);
			}
		}
	}

	public void ContentChanged() {
	}
	public void StructureChanged() {
	}
}
