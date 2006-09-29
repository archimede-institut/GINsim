package fr.univmrs.ibdm.GINsim.reg2dyn;

import java.util.Map;
import java.util.Vector;

import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsGraphEventCascade;
import fr.univmrs.ibdm.GINsim.graph.GsGraphListener;
import fr.univmrs.ibdm.GINsim.gui.GsList;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMutants;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;

/**
 * store all simulation parameters and offer a mean to access them.
 * Also deals with updating them when the graph is changed
 */
public class GsSimulationParameterList implements GsGraphListener, GsList, GsRegulatoryMutantListener {

    Vector v_parameterList = new Vector();
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
        graph.addGraphListener(this);
        GsRegulatoryMutants mutants = GsRegulatoryMutants.getMutants(this.graph);
        mutants.addListener(this);
        if (param == null) {
        	add(0);
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
        for (int i=0 ; i<v_parameterList.size() ; i++) {
            GsSimulationParameters param = (GsSimulationParameters)v_parameterList.get(i);
            if (param.m_elt != null) {
            	param.m_elt.put(data, param.v_class.get(0));
            }
        }
        return null;
    }

    public GsGraphEventCascade vertexRemoved(Object data) {
        // remove it from priority classes and initial states
    	Vector v = null;
        for (int i=0 ; i<v_parameterList.size() ; i++) {
            GsSimulationParameters param = (GsSimulationParameters)v_parameterList.get(i);
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
        for (int i=0 ; i<v_parameterList.size() ; i++) {
            GsSimulationParameters param = (GsSimulationParameters)v_parameterList.get(i);
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

    public int add(int index) {
        // find an unused name
        String s = null;
        boolean[] t = new boolean[getNbElements()];
        for (int j=0 ; j<t.length ; j++) {
            t[j] = true;
        }
        for (int j=0 ; j<t.length ; j++) {
            GsSimulationParameters param = (GsSimulationParameters)v_parameterList.get(j);
            if (param.name.startsWith("parameter ")) {
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
                s = "parameter "+(j+1);
                break;
            }
        }
        if (s == null) {
            s = "parameter "+(t.length+1);
        }

        GsSimulationParameters parameter = new GsSimulationParameters(graph.getNodeOrder());
        parameter.name = s;
        index++;
        if (index<=0 || index>v_parameterList.size()) {
            index = v_parameterList.size();
        }
    	return add(parameter, index);
    }

    public int copy(int index) {
    	if (index < 0 || index >= v_parameterList.size()) {
    		return -1;
    	}
    	GsSimulationParameters old = (GsSimulationParameters)v_parameterList.get(index);
        // find an unused name
        String s = null;
        boolean[] t = new boolean[getNbElements()];
        for (int j=0 ; j<t.length ; j++) {
            t[j] = true;
        }
        for (int j=0 ; j<t.length ; j++) {
            GsSimulationParameters param = (GsSimulationParameters)v_parameterList.get(j);
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
        if (index<=0 || index>v_parameterList.size()) {
            index = v_parameterList.size();
        }
    	return add(parameter, index);
    }

    public int add(GsSimulationParameters param, int index) {
    	v_parameterList.add(index, param);
    	return v_parameterList.indexOf(param);
    }
    public int add(GsSimulationParameters param) {
    	return add(param, v_parameterList.size());
    }
    	
    public boolean canAdd() {
        return true;
    }

    public boolean canCopy() {
        return true;
    }

    public boolean canEdit() {
        return true;
    }

    public boolean canOrder() {
        return true;
    }

    public boolean canRemove() {
        return true;
    }

    public boolean edit(int i, Object o) {
        GsSimulationParameters param = (GsSimulationParameters)v_parameterList.get(i);
        if (param.name.equals(o.toString())) {
            return false;
        }
        for (int j=0 ; j<getNbElements() ; j++) {
            if (j != i) {
                GsSimulationParameters p = (GsSimulationParameters)v_parameterList.get(j);
                if (p.name.equals(o.toString())) {
                    return false;
                }
            }
        }
        param.name = o.toString();
        return true;
    }

    public Object getElement(int i) {
        return v_parameterList.get(i);
    }

    public int getNbElements() {
        return v_parameterList.size();
    }

    public boolean moveElement(int src, int dst) {
        if (src<0 || dst<0 || src >= v_parameterList.size() || dst>=v_parameterList.size()) {
            return false;
        }
        Object o = v_parameterList.remove(src);
        v_parameterList.add(dst, o);
        return true;
    }

    public boolean remove(int[] t_index) {
        for (int i=t_index.length-1 ; i>-1 ; i--) {
            v_parameterList.remove(t_index[i]);
        }
        return true;
    }

    public void mutantAdded(Object mutant) {
    }

    public void mutantRemoved(Object mutant) {
        for (int i=0 ; i< v_parameterList.size() ; i++) {
            GsSimulationParameters param = (GsSimulationParameters)v_parameterList.get(i);
            if (param.mutant == mutant) {
                param.mutant = null;
            }
        }
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
