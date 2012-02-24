package org.ginsim.core.graph.regulatorygraph.mutant;

import java.util.Vector;

import org.ginsim.core.GraphEventCascade;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.GraphChangeType;
import org.ginsim.core.graph.common.GraphListener;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.utils.data.SimpleGenericList;


/**
 * Associate a list of mutants to the regulatory graph, and offer the UI to edit this list.
 */
public class RegulatoryMutants extends SimpleGenericList implements GraphListener<RegulatoryGraph> {

    private final RegulatoryGraph graph;
    
    /**
     * edit mutants associated with the graph
     * @param graph
     */
    public RegulatoryMutants( RegulatoryGraph graph) {
        this.graph = graph;
        GraphManager.getInstance().addGraphListener( graph, this);
        
        prefix = "mutant_";
        canAdd = true;
        canOrder = true;
        canRemove = true;
        canEdit = true;
    }
    
    @Override
    public GraphEventCascade graphChanged (RegulatoryGraph G, GraphChangeType type, Object data) {
    	if (type == GraphChangeType.NODEREMOVED) {
	        Vector v = new Vector();
	        for (int i=0 ; i<v_data.size() ; i++) {
	            RegulatoryMutantDef m = (RegulatoryMutantDef)v_data.get(i);
	            for (int j=0 ; j<m.getChanges().size() ; j++) {
	                RegulatoryMutantChange change = (RegulatoryMutantChange)m.getChange( j);
	                if (change.getNode() == data) {
	                    m.removeChange( change);
	                    v.add(m);
	                }
	            }
	        }
	        if (v.size() > 0) {
	            return new MutantCascadeUpdate (v);
	        }
    	} else if (type == GraphChangeType.NODEUPDATED) {
            Vector v = new Vector();
            for (int i=0 ; i<v_data.size() ; i++) {
                RegulatoryMutantDef m = (RegulatoryMutantDef)v_data.get(i);
                for (int j=0 ; j<m.getChanges().size() ; j++) {
                    RegulatoryMutantChange change = (RegulatoryMutantChange)m.getChange(j);
                    if (change.getNode() == data) {
                        // check that it is up to date
                        RegulatoryNode vertex = (RegulatoryNode)data;
                        if (change.getMax() > vertex.getMaxValue()) {
                            change.setMax( vertex.getMaxValue());
                            if (change.getMin() > vertex.getMaxValue()) {
                                change.setMin( vertex.getMaxValue());
                            }
                            v.add(m);
                        }
                    }
                }
            }
            if (v.size() > 0) {
                return new MutantCascadeUpdate (v);
            }
    	}
        return null;
    }
    
    /**
     * @param o
     * @return the index of o, -1 if not found
     */
    public int indexOf(Object o) {
        return v_data.indexOf(o);
    }

    /**
     * get a mutant by its name.
     * @param value
     * @return the correct mutant, or null if none.
     */
    public RegulatoryMutantDef get(String value) {
        for (int i=0 ; i<v_data.size() ; i++) {
            RegulatoryMutantDef mdef = (RegulatoryMutantDef)v_data.get(i);
            if (mdef.getName().equals(value)) {
                return mdef;
            }
        }
        return null;
    }

	protected Object doCreate(String name, int mode) {
        RegulatoryMutantDef m = new RegulatoryMutantDef();
        m.setName( name);
        graph.fireGraphChange(GraphChangeType.ASSOCIATEDUPDATED, this);
		return m;
	}

	public RegulatoryGraph getGraph() {
		return graph;
	}
}

class MutantCascadeUpdate implements GraphEventCascade {
    protected MutantCascadeUpdate(Vector v) {
        this.v = v;
    }
    Vector v;

    public String toString() {
        StringBuffer s = new StringBuffer("updated mutants:");
        for (int i=0 ; i<v.size() ; i++) {
            s.append(" ");
            s.append(v.get(i));
        }
        return s.toString();
    }
}
