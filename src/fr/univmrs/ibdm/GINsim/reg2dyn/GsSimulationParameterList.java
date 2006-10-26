package fr.univmrs.ibdm.GINsim.reg2dyn;

import java.util.Map;
import java.util.Vector;

import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsGraphEventCascade;
import fr.univmrs.ibdm.GINsim.graph.GsGraphListener;
import fr.univmrs.ibdm.GINsim.gui.GsListAbstract;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMutants;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;

/**
 * store all simulation parameters and offer a mean to access them.
 * Also deals with updating them when the graph is changed
 */
public class GsSimulationParameterList extends GsListAbstract implements GsGraphListener, GsRegulatoryMutantListener {

    String s_current;
    GsRegulatoryGraph graph;

    /**
     * @param graph
     */
    public GsSimulationParameterList(GsGraph graph) {
    	this(graph, null);
    }

    public GsSimulationParameterList(GsGraph graph, GsSimulationParameters param) {
        this.graph = (GsRegulatoryGraph)graph;
    	prefix = "parameter_";
    	canAdd = true;
    	canEdit = true;
    	canRemove = true;
    	canOrder = true;
        graph.addGraphListener(this);
        GsRegulatoryMutants mutants = GsRegulatoryMutants.getMutants(this.graph);
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

    public GsGraphEventCascade vertexRemoved(Object data) {
        // remove it from priority classes and initial states
    	Vector v = null;
        for (int i=0 ; i<v_data.size() ; i++) {
            GsSimulationParameters param = (GsSimulationParameters)v_data.get(i);
            if (param.initStates != null) {
                for (int j=0 ; j<param.initStates.size() ; j++) {
                	Map m = (Map)param.initStates.get(j);
                	if (m.containsKey(data)) {
                		m.remove(data);
                		if (v == null) {
                			v = new Vector();
                		}
                		v.add(param);
                	}
                }
            }
            if (param.m_elt != null) {
                param.m_elt.remove(data);
            }
        }
        if (v != null) {
        	return new SimulationParameterCascadeUpdate(v);
        }
        return null;
    }

    public GsGraphEventCascade vertexUpdated(Object data) {
        // remove unavailable values from initial states
        GsRegulatoryVertex vertex = (GsRegulatoryVertex)data;
    	Vector v = null;
        for (int i=0 ; i<v_data.size() ; i++) {
            GsSimulationParameters param = (GsSimulationParameters)v_data.get(i);
            if (param.initStates != null) {
                for (int j=0 ; j<param.initStates.size() ; j++) {
                	Map m = (Map)param.initStates.get(j);
                    Vector v_val = (Vector)m.get(data);
                    if (v_val != null) {
                        for (int k=v_val.size()-1 ; k>-1 ; k--) {
                            Integer val = (Integer)v_val.get(k);
                            if (val.intValue() > vertex.getMaxValue()) {
                                v_val.remove(k);
                                if (v_val.size() == 0) {
                                	m.remove(data);
                                	if (m.isEmpty()) {
                                		param.initStates.remove(m);
                                	}
                                }
                        		if (v == null) {
                        			v = new Vector();
                        		}
                        		v.add(param);
                            }
                        }
                    }
                }
            }
        }
        if (v != null) {
        	return new SimulationParameterCascadeUpdate(v);
        }
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

	public Vector getObjectType() {
		return null;
	}

	protected Object doCreate(String name, int type) {
        GsSimulationParameters parameter = new GsSimulationParameters(graph.getNodeOrder());
        parameter.name = name;
		return parameter;
	}
}

class SimulationParameterCascadeUpdate implements GsGraphEventCascade {
    protected SimulationParameterCascadeUpdate(Vector v) {
        this.v = v;
    }
    Vector v;

    public String toString() {
        StringBuffer s = new StringBuffer("updated parameters:");
        for (int i=0 ; i<v.size() ; i++) {
            s.append(" ");
            s.append(v.get(i));
        }
        return s.toString();
    }
}
