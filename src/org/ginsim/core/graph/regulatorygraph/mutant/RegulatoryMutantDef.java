package org.ginsim.core.graph.regulatorygraph.mutant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.ginsim.common.xml.XMLWriter;
import org.ginsim.core.annotation.Annotation;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;
import org.ginsim.core.utils.data.NamedObject;

import fr.univmrs.tagc.javaMDD.MDDFactory;


/**
 * store the definition of a mutant
 */
public class RegulatoryMutantDef implements NamedObject, Perturbation {
    String name;
    List<RegulatoryMutantChange> v_changes = new ArrayList<RegulatoryMutantChange>();
    Annotation annotation = new Annotation();

    boolean check(RegulatoryGraph graph) {
    	
        for (int i=0 ; i<v_changes.size() ; i++) {
            RegulatoryMutantChange nc = (RegulatoryMutantChange)v_changes.get(i);
            if (!graph.containsNode(nc.vertex)) {
                return false;
            }
            if (nc.vertex.getMaxValue() < nc.max) {
                nc.setMax(nc.vertex.getMaxValue());
            }
        }
        return true;
    }
    
    public List<RegulatoryMutantChange> getChanges() {
		return v_changes;
	}
    
    public RegulatoryMutantChange getChange( int index) {
    	
		return (RegulatoryMutantChange) v_changes.get( index);
	}
    
    public void removeChange( RegulatoryMutantChange change){
    	
    	v_changes.remove( change);
    }
    
    public void removeChange( int index){
    	
    	v_changes.remove( index);
    }
    
    
    public boolean move(int[] sel, int diff) {

        if (diff == 0 || sel == null || sel.length == 0 || 
                diff < 0 && sel[0] <= -(diff+1) ||
                diff > 0 && sel[sel.length-1] >= v_changes.size() - diff) {
            return false;
        }
        if (diff > 0) {
            doMoveDown(sel, diff);
        } else {
            doMoveUp(sel, diff);
        }
        return true;
    }
    protected void doMoveUp(int[] sel, int diff) {
        for (int i=0 ; i<sel.length ; i++) {
            int a = sel[i];
            if (a >= diff) {
                moveElement(a, a+diff);
                sel[i] += diff;
            }
        }
    }
    protected void doMoveDown(int[] sel, int diff) {
        for (int i=sel.length-1 ; i>=0 ; i--) {
            int a = sel[i];
            if (a < v_changes.size()+diff) {
                moveElement(a, a+diff);
                sel[i] += diff;
            }
        }
    }
    protected boolean moveElement(int src, int dst) {
        if (src < 0 || dst < 0 || src >= v_changes.size() || dst >= v_changes.size()) {
            return false;
        }
        RegulatoryMutantChange o = v_changes.remove(src);
        v_changes.add(dst, o);
        return true;
    }


    public String toString() {
        return name;
    }

    public int getNbChanges() {
        return v_changes.size();
    }
    
    public String getName(int index) {
        RegulatoryMutantChange change = (RegulatoryMutantChange)v_changes.get(index);
        return change.vertex.toString();
    }
    
    public byte getMin(int index) {
        return ((RegulatoryMutantChange)v_changes.get(index)).getMin();
    }

    public byte getMax(int index) {
        return ((RegulatoryMutantChange)v_changes.get(index)).getMax();
    }
    public void setMin(int index, byte val) {
        ((RegulatoryMutantChange)v_changes.get(index)).setMin(val);
    }

    public void setMax(int index, byte val) {
        ((RegulatoryMutantChange)v_changes.get(index)).setMax(val);
    }
    
    public void addChange(RegulatoryNode vertex) {
        if (vertex != null) {
            addChange(vertex, (byte)0, vertex.getMaxValue());
        }
    }
    public void addChange(RegulatoryNode vertex, byte min, byte max) {
        RegulatoryMutantChange change = new RegulatoryMutantChange(vertex);
        change.setMin(min);
        change.setMax(max);
        v_changes.add(change);
    }

    public void apply(OMDDNode[] t_tree, RegulatoryGraph graph) {
        for (int i=0 ; i<v_changes.size() ; i++) {
            RegulatoryMutantChange change = (RegulatoryMutantChange)v_changes.get(i);
            int index = graph.getNodeOrderForSimulation().indexOf(change.vertex);
            t_tree[index] = change.apply(t_tree[index], graph);
        }
    }

    public void toXML(XMLWriter out) throws IOException {
        out.openTag("mutant");
        out.addAttr("name", name);
        for (int i=0 ; i<v_changes.size() ; i++) {
            ((RegulatoryMutantChange)v_changes.get(i)).toXML(out);
        }
        annotation.toXML(out, null, 0);
        out.closeTag();
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCondition(int index) {
		return ((RegulatoryMutantChange)v_changes.get(index)).getCondition();
	}
	
	public void setCondition(int index, RegulatoryGraph graph, String condition) {
		((RegulatoryMutantChange)v_changes.get(index)).setCondition(condition, graph);
	}
	
	public Annotation getAnnotation() {
		return annotation;
	}

	public boolean hasValidCondition(int index) {
		if (index >= v_changes.size()) {
			return true;
		}
		return ((RegulatoryMutantChange)v_changes.get(index)).s_condition == null;
	}

	@Override
	public int[] apply(MDDFactory factory, int[] nodes, RegulatoryGraph graph) {
		int[] result = nodes.clone();
		
        for (int i=0 ; i<v_changes.size() ; i++) {
            RegulatoryMutantChange change = (RegulatoryMutantChange)v_changes.get(i);
            int index = graph.getNodeOrderForSimulation().indexOf(change.vertex);
            result[index] = change.apply(factory, result[index], graph);
        }

		return result;
	}
}