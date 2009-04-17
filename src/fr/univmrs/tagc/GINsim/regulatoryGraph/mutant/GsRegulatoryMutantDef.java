package fr.univmrs.tagc.GINsim.regulatoryGraph.mutant;

import java.io.IOException;
import java.util.Vector;

import fr.univmrs.tagc.GINsim.annotation.Annotation;
import fr.univmrs.tagc.GINsim.graph.GsGraphManager;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.tagc.common.datastore.NamedObject;
import fr.univmrs.tagc.common.xml.XMLWriter;

/**
 * store the definition of a mutant
 */
public class GsRegulatoryMutantDef implements NamedObject {
    String name;
    Vector v_changes = new Vector();
    Annotation annotation = new Annotation();

    boolean check(GsRegulatoryGraph graph) {
        GsGraphManager gm = graph.getGraphManager();
        for (int i=0 ; i<v_changes.size() ; i++) {
            GsRegulatoryMutantChange nc = (GsRegulatoryMutantChange)v_changes.get(i);
            if (!gm.containsVertex(nc.vertex)) {
                return false;
            }
            if (nc.vertex.getMaxValue() < nc.max) {
                nc.setMax(nc.vertex.getMaxValue());
            }
        }
        return true;
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
        Object o = v_changes.remove(src);
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
        GsRegulatoryMutantChange change = (GsRegulatoryMutantChange)v_changes.get(index);
        return change.vertex.toString();
    }
    
    public byte getMin(int index) {
        return ((GsRegulatoryMutantChange)v_changes.get(index)).getMin();
    }

    public byte getMax(int index) {
        return ((GsRegulatoryMutantChange)v_changes.get(index)).getMax();
    }
    public void setMin(int index, byte val) {
        ((GsRegulatoryMutantChange)v_changes.get(index)).setMin(val);
    }

    public void setMax(int index, byte val) {
        ((GsRegulatoryMutantChange)v_changes.get(index)).setMax(val);
    }
    
    public void addChange(GsRegulatoryVertex vertex) {
        if (vertex != null) {
            addChange(vertex, (byte)0, vertex.getMaxValue());
        }
    }
    public void addChange(GsRegulatoryVertex vertex, byte min, byte max) {
        GsRegulatoryMutantChange change = new GsRegulatoryMutantChange(vertex);
        change.setMin(min);
        change.setMax(max);
        v_changes.add(change);
    }

    /**
     * apply this mutant on the OMDD.
     * 
     * @param t_tree OMDD for all genes of the model
     * @param graph the regulatory graph
     */
    public void apply(OmddNode[] t_tree, GsRegulatoryGraph graph) {
        for (int i=0 ; i<v_changes.size() ; i++) {
            GsRegulatoryMutantChange change = (GsRegulatoryMutantChange)v_changes.get(i);
            int index = graph.getNodeOrderForSimulation().indexOf(change.vertex);
            t_tree[index] = change.apply(t_tree[index], graph);
        }
    }

    public void toXML(XMLWriter out) throws IOException {
        out.openTag("mutant");
        out.addAttr("name", name);
        for (int i=0 ; i<v_changes.size() ; i++) {
            ((GsRegulatoryMutantChange)v_changes.get(i)).toXML(out);
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
		return ((GsRegulatoryMutantChange)v_changes.get(index)).getCondition();
	}
	
	public void setCondition(int index, GsRegulatoryGraph graph, String condition) {
		((GsRegulatoryMutantChange)v_changes.get(index)).setCondition(condition, graph);
	}
	
	public Annotation getAnnotation() {
		return annotation;
	}

	public boolean hasValidCondition(int index) {
		if (index >= v_changes.size()) {
			return true;
		}
		return ((GsRegulatoryMutantChange)v_changes.get(index)).s_condition == null;
	}
}