package fr.univmrs.tagc.GINsim.reg2dyn;

import fr.univmrs.tagc.GINsim.dynamicGraph.GsDynamicNode;


public class SimulationQueuedState {
	public int[] state;
	public GsDynamicNode previous = null;
	public boolean multiple = false;
	public int depth;
	
	SimulationQueuedState(int[] state, int depth, GsDynamicNode previous, boolean multiple) {
		this.state = state;
		this.previous = previous;
		this.depth = depth;
		this.multiple = multiple;
	}
}

