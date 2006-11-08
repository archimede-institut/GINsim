package fr.univmrs.ibdm.GINsim.reg2dyn;

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
        	return new SimulationParameterCascadeUpdate(v);
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
        	return new SimulationParameterCascadeUpdate(v);
        }
        return null;
	}
}
