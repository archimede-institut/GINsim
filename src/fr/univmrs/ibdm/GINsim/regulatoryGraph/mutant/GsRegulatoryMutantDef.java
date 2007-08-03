package fr.univmrs.ibdm.GINsim.regulatoryGraph.mutant;

import java.io.IOException;
import java.util.Vector;

import fr.univmrs.ibdm.GINsim.data.GsAnnotation;
import fr.univmrs.ibdm.GINsim.global.GsNamedObject;
import fr.univmrs.ibdm.GINsim.graph.GsGraphManager;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.ibdm.GINsim.xml.GsXMLWriter;

/**
 * store the definition of a mutant
 */
public class GsRegulatoryMutantDef implements GsNamedObject {
    String name;
    Vector v_changes = new Vector();
    GsAnnotation annotation = new GsAnnotation();

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
    
    public short getMin(int index) {
        return ((GsRegulatoryMutantChange)v_changes.get(index)).getMin();
    }

    public short getMax(int index) {
        return ((GsRegulatoryMutantChange)v_changes.get(index)).getMax();
    }
    void setMin(int index, short val) {
        ((GsRegulatoryMutantChange)v_changes.get(index)).setMin(val);
    }

    void setMax(int index, short val) {
        ((GsRegulatoryMutantChange)v_changes.get(index)).setMax(val);
    }
    
    void addChange(GsRegulatoryVertex vertex) {
        if (vertex != null) {
            addChange(vertex, (short)0, vertex.getMaxValue());
        }
    }
    void addChange(GsRegulatoryVertex vertex, short min, short max) {
        GsRegulatoryMutantChange change = new GsRegulatoryMutantChange(vertex);
        change.setMin(min);
        change.setMax(max);
        v_changes.add(change);
    }

    /**
     * apply this mutant on the OMDD.
     * 
     * @param t_tree OMDD for all genes of the model
     * @param listGenes the list of the genes
     */
    public void apply(OmddNode[] t_tree, Vector listGenes, boolean isstrict) {
        for (int i=0 ; i<v_changes.size() ; i++) {
            GsRegulatoryMutantChange change = (GsRegulatoryMutantChange)v_changes.get(i);
            int index = listGenes.indexOf(change.vertex);
            t_tree[index] = change.apply(t_tree[index], listGenes, isstrict);
        }
    }

    public void toXML(GsXMLWriter out) throws IOException {
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
}