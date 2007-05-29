package fr.univmrs.ibdm.GINsim.regulatoryGraph.initialState;

import java.util.Vector;

import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsGraphEventCascade;
import fr.univmrs.ibdm.GINsim.graph.GsGraphListener;
import fr.univmrs.ibdm.GINsim.gui.GsListAbstract;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;

public class GsInitialStateList extends GsListAbstract implements GsGraphListener {
	GsRegulatoryGraph graph;
	
    public GsInitialStateList(GsGraph graph) {
        this.graph = (GsRegulatoryGraph)graph;
    	prefix = "initState_";
    	canAdd = true;
    	canEdit = true;
    	canRemove = true;
    	canOrder = true;
        graph.addGraphListener(this);
    }

	protected Object doCreate(String name, int type) {
		GsInitialState i = new GsInitialState();
		i.setName(name);
		return i;
	}

	public Vector getObjectType() {
		return null;
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
		return null;
	}

	public GsGraphEventCascade graphMerged(Object data) {
		return null;
	}

	public GsGraphEventCascade vertexRemoved(Object data) {
        // remove it from initial states
    	Vector v = null;
        for (int i=0 ; i<v_data.size() ; i++) {
        	GsInitialState is = (GsInitialState)v_data.get(i);
        	if (is.m.containsKey(data)) {
        		is.m.remove(data);
        		if (v == null) {
        			v = new Vector();
        		}
        		v.add(is);
            }
        }
        if (v != null) {
        	return new InitialStateCascadeUpdate(v);
        }
        return null;
	}

	public GsGraphEventCascade vertexUpdated(Object data) {
        // remove unavailable values from initial states
        GsRegulatoryVertex vertex = (GsRegulatoryVertex)data;
    	Vector v = null;
        for (int i=0 ; i<v_data.size() ; i++) {
        	GsInitialState is = (GsInitialState)v_data.get(i);
            Vector v_val = (Vector)is.m.get(data);
            if (v_val != null) {
                for (int k=v_val.size()-1 ; k>-1 ; k--) {
                    Integer val = (Integer)v_val.get(k);
                    if (val.intValue() > vertex.getMaxValue()) {
                        v_val.remove(k);
                        if (v_val.size() == 0) {
                        	is.m.remove(data);
                        	if (is.m.isEmpty()) {
                        		remove(new int[] {i});
                        	}
                        }
                		if (v == null) {
                			v = new Vector();
                		}
                		v.add(is);
                    }
                }
            }
        }
        if (v != null) {
        	return new InitialStateCascadeUpdate(v);
        }
        return null;
	}

	public Object getInitState(String s) {
    	for (int i=0 ; i<getNbElements() ; i++) {
    		GsInitialState istate = (GsInitialState)getElement(i);
    		if (istate.getName().equals(s)) {
    			return istate;
    		}
    	}
		return null;
	}
}

class InitialStateCascadeUpdate implements GsGraphEventCascade {
    protected InitialStateCascadeUpdate(Vector v) {
        this.v = v;
    }
    Vector v;

    public String toString() {
        StringBuffer s = new StringBuffer("updated initial states:");
        for (int i=0 ; i<v.size() ; i++) {
            s.append(" ");
            s.append(v.get(i));
        }
        return s.toString();
    }
}
