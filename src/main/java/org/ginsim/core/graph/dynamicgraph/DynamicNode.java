package org.ginsim.core.graph.dynamicgraph;

import java.util.Collection;

import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.graph.view.NodeShape;


/**
 * a node in a state transition graph (ie a possible state of the regulatory graph).
 */
public class DynamicNode {
	/** each int is the state of the corresponding gene in the regulatory graph (according to nodeOrder). */
	public final byte[] state;
	/** if this is a stable node, default to false */
	private boolean stable = false;

	/** pattern constants */
	public final int PLUS = 1;
	public final int MINUS = 2;
	public final int EPSILON = 0; //No commutation
	
	/**
	 * @param state
	 */
	public DynamicNode(byte[] state) {
		this.state = state;
	}
	/**
	 * create a dynamicNode from it's id: the first character is ignored (must be a letter to be a valid id)
	 * each following character is parsed as int
	 * @param value
	 */
	public DynamicNode (String value) {
		state = new byte[value.length()-1];
		for (int i=0 ; i<state.length ; i++) {
			state[i] = (byte) (value.charAt(i+1) - '0');
		}
	}
	/**
	 * is this a stable state ?
	 * @return true if the node is stable
	 */
	public boolean isStable() {
		return stable;
	}
	/**
	 * set if this state is stable.
	 * @param s
	 * @param vreader 
	 */
	public void setStable(boolean s, NodeAttributesReader vreader) {
		stable = s;
		if (stable) {
			vreader.setNode(this);
		}
	}
	
	public String toString() {
		String s = "";
		for (int i=0 ; i<state.length ; i++)
			s += ""+state[i];
		return s;
	}
	/**
	 * @return the node's id.
	 */
	public String getId() {
		return "s"+this.toString();
	}

	/**
	 * @return the node's pattern of commutation.
	 */
	public int[] getPattern(Graph<DynamicNode, Edge<DynamicNode>> graph) {
		int[] pattern = new int[state.length];
		Collection<Edge<DynamicNode>> oe = graph.getOutgoingEdges(this);
		for (Edge<DynamicNode> e: oe) {
			DynamicNode s = e.getTarget();
			for (int i = 0; i < s.state.length; i++) {
				if (s.state[i] < this.state[i]) pattern[i] = MINUS;
				else if (s.state[i] > this.state[i]) pattern[i] = PLUS;
			}
		}
		return pattern;
	}
	/**
	 * @return the node's pattern of commutation.
	 */
	public String getPatternString( Graph<DynamicNode, Edge<DynamicNode>> graph) {
		int[] pattern = getPattern(graph);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < pattern.length; i++) {
			if (pattern[i] == EPSILON) sb.append('e');
			else if (pattern[i] == PLUS) sb.append('+');
			else if (pattern[i] == MINUS) sb.append('-');
		}
		return sb.toString();
	}

	
	public boolean equals (Object obj) {
		if (!(obj instanceof DynamicNode)) {
			return false;
		}
		byte[] ostate = ((DynamicNode)obj).state;
		if (ostate.length != state.length) {
			return false;
		}
		for (int i=0 ; i<state.length ; i++) {
			if (ostate[i] != state[i]) {
				return false;
			}
		}
		return true;
	}
	
	public int hashCode() {
		int h = 0;
		for (int i=0 ; i<state.length ; i++) {
			h += i*i*state[i];
		}		
		return h;
	}
}
