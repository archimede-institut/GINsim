package fr.univmrs.ibdm.GINsim.reg2dyn;

import fr.univmrs.ibdm.GINsim.dynamicGraph.GsDynamicNode;


public class SimulationQueuedState {
	int[] state;
	GsDynamicNode previous = null;
	boolean multiple = false;
	int depth;
	
	SimulationQueuedState(int[] state, int depth, GsDynamicNode previous, boolean multiple) {
		this.state = state;
		this.previous = previous;
		this.depth = depth;
		this.multiple = multiple;
	}
}

