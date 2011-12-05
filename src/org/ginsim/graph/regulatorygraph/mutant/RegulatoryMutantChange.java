package org.ginsim.graph.regulatorygraph.mutant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.ginsim.common.xml.XMLWriter;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.graph.regulatorygraph.logicalfunction.BooleanParser;
import org.ginsim.graph.regulatorygraph.logicalfunction.LogicalParameter;
import org.ginsim.graph.regulatorygraph.logicalfunction.LogicalFunctionList;
import org.ginsim.graph.regulatorygraph.logicalfunction.LogicalFunctionListElement;
import org.ginsim.graph.regulatorygraph.omdd.OMDDNode;


public class RegulatoryMutantChange {
    RegulatoryNode vertex;
    byte min;
    byte max;
    BooleanParser parser = null;
    String s_condition = null;
    
    RegulatoryMutantChange(RegulatoryNode vertex) {
        this.vertex = vertex;
        this.min = this.max = -1;
    }
    
    public byte getMin() {
        return min;
    }
    public byte getMax() {
        return max;
    }
    public void setMin(byte min) {
        if (min>vertex.getMaxValue()) {
            return;
        }
        this.min = min;
        if (min>max) {
            max = min;
        }
    }
    public void setMax(byte max) {
        if (max>vertex.getMaxValue()) {
            return;
        }
        this.max = max;
        if (max<min) {
            this.min = max;
        }
    }
    public RegulatoryNode getNode() {
		return vertex;
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
				parser = new BooleanParser(graph.getIncomingEdges(vertex));
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return;
			}
    	}
    	if (!parser.compile(condition, graph, vertex)) {
    		s_condition = condition;
    	}
    }
    protected OMDDNode apply(OMDDNode node, RegulatoryGraph graph) {
        int maxValue = vertex.getMaxValue();
        if (min == 0 && max == maxValue) {
            // no change here!
            return node;
        }
        OMDDNode cst = new OMDDNode();
        cst.min = min;
        cst.max = max;
        if (parser != null) {
        	OMDDNode terminal = cst;
        	cst = OMDDNode.TERMINALS[0];
        	try {
        		LogicalFunctionList functionList = (LogicalFunctionList)parser.eval();
        		Iterator it = parser.getParams(functionList.getData()).iterator();
        		while (it.hasNext()) {
        			// FIXME: the edge list is duplicated, this is far from clean...
        			List lfunc = (List)it.next();
        			List l = new ArrayList(lfunc.size());
        			Iterator it2 = lfunc.iterator();
        			while (it2.hasNext()) {
        				LogicalFunctionListElement elem = (LogicalFunctionListElement)it2.next();
        				l.add(elem.getEdge().getEdge(elem.getIndex()));
        			}
        			LogicalParameter param = new LogicalParameter(l, 1);
        			cst = cst.merge(param.buildTree(graph, vertex, terminal), OMDDNode.CONSTRAINTOR);
        		}
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        OMDDNode result = node.merge(cst, OMDDNode.CONSTRAINT);
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