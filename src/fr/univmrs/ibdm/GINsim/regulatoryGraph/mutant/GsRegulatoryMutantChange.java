package fr.univmrs.ibdm.GINsim.regulatoryGraph.mutant;

import java.io.IOException;
import java.util.Iterator;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsLogicalParameter;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.GsBooleanParser;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.GsLogicalFunctionList;
import fr.univmrs.tagc.common.xml.XMLWriter;

class GsRegulatoryMutantChange {
    GsRegulatoryVertex vertex;
    // for now, only set min/max, more powerfull tools coming later
    short min;
    short max;
    boolean force = true;
    GsBooleanParser parser = null;
    
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

    public String getCondition() {
    	if (parser == null || parser.getRoot() == null) {
    		return "";
    	}
    	return parser.getRoot().toString();
    }
    
    public void setCondition(String condition, GsRegulatoryGraph graph) {
    	if (parser == null) {
    		try {
				parser = new GsBooleanParser(graph.getGraphManager().getIncomingEdges(vertex));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return;
			}
    	}
    	System.out.println("ready to parse it");
    	boolean ret = parser.compile(condition, graph, vertex);
    	System.out.println("result: "+ret);
    }
    protected OmddNode apply(OmddNode node, GsRegulatoryGraph graph) {
        int maxValue = vertex.getMaxValue();
        if (min == 0 && max == maxValue) {
            // no change here!
            return node;
        }
        int reflevel = graph.getNodeOrderForSimulation().indexOf(vertex);
        // TODO: apply condition with mutant
        if (parser != null) {
        	try {
        		GsLogicalFunctionList functionList = (GsLogicalFunctionList)parser.eval();
        		Iterator it = functionList.getData().iterator();
        		while (it.hasNext()) {
        			GsLogicalParameter param = (GsLogicalParameter)it.next();
        			param.buildTree(graph, vertex);
        		}
        		
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        OmddNode cst = new OmddNode();
        if (force) {
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

    public void toXML(XMLWriter out) throws IOException {
        out.openTag("change");
        out.addAttr("target", vertex.getId());
        out.addAttr("min", ""+min);
        out.addAttr("max", ""+max);
        out.closeTag();
    }
}