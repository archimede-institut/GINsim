package fr.univmrs.tagc.GINsim.regulatoryGraph.mutant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryVertex;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsLogicalParameter;
import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.GsBooleanParser;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.GsLogicalFunctionList;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.GsLogicalFunctionListElement;
import fr.univmrs.tagc.common.xml.XMLWriter;

class GsRegulatoryMutantChange {
    RegulatoryVertex vertex;
    byte min;
    byte max;
    GsBooleanParser parser = null;
    String s_condition = null;
    
    GsRegulatoryMutantChange(RegulatoryVertex vertex) {
        this.vertex = vertex;
        this.min = this.max = -1;
    }
    
    byte getMin() {
        return min;
    }
    byte getMax() {
        return max;
    }
    void setMin(byte min) {
        if (min>vertex.getMaxValue()) {
            return;
        }
        this.min = min;
        if (min>max) {
            max = min;
        }
    }
    void setMax(byte max) {
        if (max>vertex.getMaxValue()) {
            return;
        }
        this.max = max;
        if (max<min) {
            this.min = max;
        }
    }

    public String getCondition() {
		if (s_condition != null) {
			return s_condition;
		}
    	if (parser == null || parser.getRoot() == null) {
    		return "";
    	}
    	return parser.getRoot().toString();
    }
    
    public void setCondition(String condition, RegulatoryGraph graph) {
		s_condition = null;
    	if (condition == null || condition.trim() == "") {
    		parser = null;
    		return;
    	}
    	if (parser == null) {
    		try {
				parser = new GsBooleanParser(graph.getIncomingEdges(vertex));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return;
			}
    	}
    	if (!parser.compile(condition, graph, vertex)) {
    		s_condition = condition;
    	}
    }
    protected OmddNode apply(OmddNode node, RegulatoryGraph graph) {
        int maxValue = vertex.getMaxValue();
        if (min == 0 && max == maxValue) {
            // no change here!
            return node;
        }
        OmddNode cst = new OmddNode();
        cst.min = min;
        cst.max = max;
        if (parser != null) {
        	OmddNode terminal = cst;
        	cst = OmddNode.TERMINALS[0];
        	try {
        		GsLogicalFunctionList functionList = (GsLogicalFunctionList)parser.eval();
        		Iterator it = parser.getParams(functionList.getData()).iterator();
        		while (it.hasNext()) {
        			// FIXME: the edge list is duplicated, this is far from clean...
        			List lfunc = (List)it.next();
        			List l = new ArrayList(lfunc.size());
        			Iterator it2 = lfunc.iterator();
        			while (it2.hasNext()) {
        				GsLogicalFunctionListElement elem = (GsLogicalFunctionListElement)it2.next();
        				l.add(elem.getEdge().getEdge(elem.getIndex()));
        			}
        			GsLogicalParameter param = new GsLogicalParameter(l, 1);
        			cst = cst.merge(param.buildTree(graph, vertex, terminal), OmddNode.CONSTRAINTOR);
        		}
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        OmddNode result = node.merge(cst, OmddNode.CONSTRAINT);
        return result;
    }

    public void toXML(XMLWriter out) throws IOException {
        out.openTag("change");
        out.addAttr("target", vertex.getId());
        out.addAttr("min", ""+min);
        out.addAttr("max", ""+max);
        String condition = getCondition();
        if (!"".equals(condition)) {
            out.addAttr("condition", getCondition());
    	}
        out.closeTag();
    }
}