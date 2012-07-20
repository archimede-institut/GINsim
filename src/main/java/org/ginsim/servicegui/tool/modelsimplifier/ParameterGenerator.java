package org.ginsim.servicegui.tool.modelsimplifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.ginsim.core.graph.regulatorygraph.RegulatoryEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.LogicalFunctionBrowser;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.LogicalParameter;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;

public class ParameterGenerator extends LogicalFunctionBrowser {
	private List<LogicalParameter> paramList;
	private int[][] t_values;
	private RegulatoryMultiEdge[] t_me;
	private Map<RegulatoryNode, Integer> m_orderPos;
	
	public ParameterGenerator(List<RegulatoryNode> nodeOrder, Map<RegulatoryNode, Integer> m_orderPos) {
		super(nodeOrder);
		this.m_orderPos = m_orderPos;
		System.out.println("order: "+m_orderPos);
	}

	public void browse(Collection<RegulatoryMultiEdge> edges, RegulatoryNode targetNode, OMDDNode node) {
		this.paramList = new ArrayList<LogicalParameter>();
		t_values = new int[edges.size()][4];
		t_me = new RegulatoryMultiEdge[t_values.length];
		
		int i = -1;
		for (RegulatoryMultiEdge me: edges) {
			i++;
			t_me[i] = me;
			t_values[i][0] = m_orderPos.get(me.getSource());
		}

		browse(node);
		targetNode.getV_logicalParameters().setManualParameters(paramList);
	}
	
	protected void leafReached(OMDDNode leaf) {
		if (leaf.value == 0) {
			return;
		}
		// transform constraints on values to constraints on edges
		for (int i=0 ; i<t_values.length ; i++) {
			int nb = t_values[i][0];
			int begin = path[nb][0];
			int end = path[nb][1];
			RegulatoryMultiEdge me = t_me[i];
			nb = me.getEdgeCount();
			
			if (begin == -1) {
				// all values are allowed
				t_values[i][1] = -1;
				t_values[i][2] = nb-1;
			} else {
				// find the first edge
				if (begin == 0) {
					// start before the first edge
					t_values[i][1] = -1;
				} else {
					// lookup the start
					for (int j=0 ; j<nb ; j++) {
						if (me.getMin(j) >= begin) {
							t_values[i][1] = j;
							break;
						}
					}
				}
				// find the last edge
				for (int j=t_values[i][1] ; j<nb ; j++) {
					if (j == -1) {
						if (end < me.getMin(0)) {
							t_values[i][2] = -1;
							break;
						}
						continue;
					}
					int max = me.getMax(j);
					if (max == -1 || end <= max) {
						t_values[i][2] = j;
						break;
					}
				}
			}
		}
		
		// prepare to iterate through logical parameters
		for (int i=0 ; i<t_values.length ; i++) {
			t_values[i][3] = t_values[i][1];
		}
		
		while (true) {
			List<RegulatoryEdge> l = new ArrayList<RegulatoryEdge>();
			int lastIndex = -1;
			for (int i=0 ; i<t_values.length ; i++) {
				if (t_values[i][3] != -1) {
					// add interaction to the vector
					l.add(t_me[i].getEdge(t_values[i][3]));
				}
				if (t_values[i][3] < t_values[i][2]) {
					lastIndex = i;
				}
			}
			
			paramList.add(new LogicalParameter(l, leaf.value));

			// stop if no free value was found
			if (lastIndex == -1) {
				break;
			}
			// go to next step
			t_values[lastIndex][3]++;
			for (int i=lastIndex+1 ; i<t_values.length ; i++) {
				t_values[i][3] = t_values[i][1];
			}
		}
	}
}

