package fr.univmrs.tagc.GINsim.dynamicGraph;

import fr.univmrs.tagc.GINsim.graph.GsVertexAttributesReader;


/**
 * a vertex in a state transition graph (ie a possible state of the regulatory graph).
 */
public final class GsDynamicNode {
	/** each int is the state of the corresponding gene in the regulatory graph (according to nodeOrder). */
	public final int[] state;
	/** if this is a stable node, default to false */
	private boolean stable = false;

	/**
	 * @param state
	 */
	public GsDynamicNode(int[] state) {
		this.state = state;
	}
	/**
	 * create a dynamicNode from it's id: the first character is ignored (must be a letter to be a valid id)
	 * each following character is parsed as int
	 * @param value
	 */
	public GsDynamicNode (String value) {
		state = new int[value.length()-1];
		for (int i=0 ; i<state.length ; i++) {
			state[i] = value.charAt(i+1) - '0';
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
	public void setStable(boolean s, GsVertexAttributesReader vreader) {
		stable = s;
		if (stable) {
			vreader.setVertex(this);
			vreader.setShape(vreader.getDefaultVertexShape() == GsVertexAttributesReader.SHAPE_RECTANGLE ? GsVertexAttributesReader.SHAPE_ELLIPSE: GsVertexAttributesReader.SHAPE_RECTANGLE);
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
	
	public boolean equals (Object obj) {
		if (!(obj instanceof GsDynamicNode)) {
			return false;
		}
		int[] ostate = ((GsDynamicNode)obj).state;
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
