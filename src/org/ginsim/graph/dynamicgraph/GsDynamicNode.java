package org.ginsim.graph.dynamicgraph;

import java.util.Collection;

import org.ginsim.graph.common.Edge;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.common.VertexAttributesReader;


/**
 * a vertex in a state transition graph (ie a possible state of the regulatory graph).
 */
public final class GsDynamicNode {
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
	public GsDynamicNode(byte[] state) {
		this.state = state;
	}
	/**
	 * create a dynamicNode from it's id: the first character is ignored (must be a letter to be a valid id)
	 * each following character is parsed as int
	 * @param value
	 */
	public GsDynamicNode (String value) {
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
	public void setStable(boolean s, VertexAttributesReader vreader) {
		stable = s;
		if (stable) {
			vreader.setVertex(this);
			vreader.setShape(vreader.getDefaultVertexShape() == VertexAttributesReader.SHAPE_RECTANGLE ? VertexAttributesReader.SHAPE_ELLIPSE: VertexAttributesReader.SHAPE_RECTANGLE);
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
	public int[] getPattern(Graph<GsDynamicNode, Edge<GsDynamicNode>> graph) {
		int[] pattern = new int[state.length];
		Collection<Edge<GsDynamicNode>> oe = graph.getOutgoingEdges(this);
		for (Edge<GsDynamicNode> e: oe) {
			GsDynamicNode s = e.getTarget();
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
	public String getPatternString( Graph<GsDynamicNode, Edge<GsDynamicNode>> graph) {
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
		if (!(obj instanceof GsDynamicNode)) {
			return false;
		}
		byte[] ostate = ((GsDynamicNode)obj).state;
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
