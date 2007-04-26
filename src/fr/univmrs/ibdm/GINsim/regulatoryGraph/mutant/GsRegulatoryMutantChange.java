package fr.univmrs.ibdm.GINsim.regulatoryGraph.mutant;

import java.io.IOException;
import java.util.Vector;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.ibdm.GINsim.xml.GsXMLWriter;

class GsRegulatoryMutantChange {
    GsRegulatoryVertex vertex;
    // for now, only set min/max, more powerfull tools coming later
    short min;
    short max;
    
    GsRegulatoryMutantChange(GsRegulatoryVertex vertex) {
        this.vertex = vertex;
        this.min = this.max = -1;
    }
    
    short getMin() {
        return min;
    }
    short getMax() {
        return max;
    }
    void setMin(short min) {
        if (min>vertex.getMaxValue()) {
            return;
        }
        this.min = min;
        if (min>max) {
            max = min;
        }
    }
    void setMax(short max) {
        if (max>vertex.getMaxValue()) {
            return;
        }
        this.max = max;
        if (max<min) {
            this.min = max;
        }
    }

    protected OmddNode apply(OmddNode node, Vector nodeOrder, boolean isstrict) {
        int maxValue = vertex.getMaxValue();
        if (min == 0 && max == maxValue) {
            // no change here!
            return node;
        }
        int reflevel = nodeOrder.indexOf(vertex);
        OmddNode cst = new OmddNode();
        if (isstrict) {
            cst.min = min;
            cst.max = max;
        } else {
	        cst.level = reflevel;
	        cst.next = new OmddNode[maxValue+1];
	        for (int i=0 ; i<cst.next.length ; i++) {
	            if (i==min && i!=0) {
	                OmddNode nMin = new OmddNode();
	                nMin.min = min;
	                nMin.max = max;
	                cst.next[i] = nMin;
	            } else if (i==max && max < maxValue) {
	                OmddNode nMax = new OmddNode();
	                nMax.min = min;
	                nMax.max = max;
	                cst.next[i] = nMax;
	            } else {
	                cst.next[i] = OmddNode.TERMINALS[0];
	            }
	        }
        }
        return node.merge(cst, OmddNode.CONSTRAINT);
    }

    public void toXML(GsXMLWriter out) throws IOException {
        out.openTag("change");
        out.addAttr("target", vertex.getId());
        out.addAttr("min", ""+min);
        out.addAttr("max", ""+max);
        out.closeTag();
    }
}