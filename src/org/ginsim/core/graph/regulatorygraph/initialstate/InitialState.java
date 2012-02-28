package org.ginsim.core.graph.regulatorygraph.initialstate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;
import org.ginsim.core.utils.data.NamedObject;

import fr.univmrs.tagc.javaMDD.MDDFactory;
import fr.univmrs.tagc.javaMDD.MDDOperator;
import fr.univmrs.tagc.javaMDD.operators.MDDBaseOperators;


public class InitialState implements NamedObject {
	String name;
	Map<RegulatoryNode, List<Integer>> m = new HashMap<RegulatoryNode, List<Integer>>();
	
    public void setState(int[] state, List<RegulatoryNode> nodeOrder) {
        setState(state, nodeOrder, false);
    }
    public void setState(int[] state, List<RegulatoryNode> nodeOrder, boolean input) {
        String[] t_s = new String[state.length];
        for (int i=0 ; i<t_s.length ; i++) {
            RegulatoryNode vertex = (RegulatoryNode)nodeOrder.get(i);
            if (vertex.isInput() == input) {
                t_s[i] = vertex + ";" + state[i];
            } else {
                t_s[i] = "";
            }
        }
        setData(t_s, nodeOrder);
    }
    
    public Map<RegulatoryNode, List<Integer>> getMaxValueTable() {
    	
		return m;
	}
    
    public void setMaxValueTable( Map<RegulatoryNode, List<Integer>> m) {
    	
		this.m = m;
	}
    
	public void setData(String[] t_s, List<RegulatoryNode> nodeOrder) {
        for (int i=0 ; i<t_s.length ; i++) {
            RegulatoryNode vertex = null;
            String[] t_val = t_s[i].split(";");
            if (t_val.length > 1) {
                for (int j=0 ; j<nodeOrder.size() ; j++) {
                    if (((RegulatoryNode)nodeOrder.get(j)).getId().equals(t_val[0])) {
                        vertex = (RegulatoryNode)nodeOrder.get(j);
                        break;
                    }
                }
                if (vertex != null) {
                	List<Integer> v_val = new ArrayList<Integer>();
                    for (int j=1 ; j<t_val.length ; j++) {
                        try {
                        	int v = Integer.parseInt(t_val[j]);
                            if (v >= 0 && v <= vertex.getMaxValue()) {
                                boolean ok = true;
                                for (int k=0 ; k<v_val.size() ; k++) {
                                    if (v_val.get(k).equals(v)) {
                                        ok = false;
                                        break;
                                    }
                                }
                                if (ok) {
                                    v_val.add(v);
                                }
                            } else {
                                // TODO: report error in file
                            }
                        } catch (NumberFormatException e) {
                            // TODO: report error in file
                        }
                    }
                    if (!v_val.isEmpty() && v_val.size() <= vertex.getMaxValue()) {
                        m.put(vertex, v_val);
                    }
                }
            }
        }
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Map<RegulatoryNode,List<Integer>> getMap() {
		return m;
	}
	public OMDDNode getMDD(List<RegulatoryNode> nodeOrder) {
		OMDDNode falseNode = OMDDNode.TERMINALS[0];
		OMDDNode ret = OMDDNode.TERMINALS[1];
		for (int i=nodeOrder.size()-1 ; i>-1 ; i--) {
			RegulatoryNode vertex = (RegulatoryNode)nodeOrder.get(i);
			Object o = m.get(vertex);
			if (o != null) {
				OMDDNode newNode = new OMDDNode();
				newNode.level = i;
				newNode.next = new OMDDNode[vertex.getMaxValue()+1];
				
				for (int v=0 ; v<newNode.next.length ; v++) {
					newNode.next[v] = falseNode;
				}
				List<Integer> l_val = m.get(vertex);
				for (int n: l_val) {
					newNode.next[n] = ret;
				}
				ret = newNode;
			}
		}
		return ret;
	}
	public int getMDD(MDDFactory factory) {

		int[] nodes = new int[m.size()];
		int idx = 0;
		for (Entry<RegulatoryNode, List<Integer>> e: m.entrySet()) {
			RegulatoryNode node = e.getKey();
			List<Integer> values = e.getValue();
			if (values == null) {
				nodes[idx++] = 1;
				continue;
			}
			
			int level = factory.getVariableID(node);
			int[] next = new int[node.getMaxValue()+1];
			// just to be sure: reset the array
			for (int v=0 ; v<next.length ; v++) {
				next[v] = 0;
			}
			
			// set valid values
			List<Integer> l_val = m.get(node);
			for (int n: l_val) {
				next[n] = 1;
			}
			
			nodes[idx++] = factory.get_mnode(level, next);
		}
		return MDDBaseOperators.AND.combine(factory, nodes);
	}
}
