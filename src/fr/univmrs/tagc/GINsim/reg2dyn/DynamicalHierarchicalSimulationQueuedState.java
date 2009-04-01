package fr.univmrs.tagc.GINsim.reg2dyn;

import fr.univmrs.tagc.GINsim.dynamicalHierachicalGraph.GsDynamicalHierarchicalNode;

public class DynamicalHierarchicalSimulationQueuedState {
	public int[] state;
	public DynamicalHierarchicalSimulationQueuedState previous = null;
	public GsDynamicalHierarchicalNode dhnode = null;
	public boolean multiple = false;
	public int depth;

	public DynamicalHierarchicalSimulationQueuedState(int[] state, int depth, DynamicalHierarchicalSimulationQueuedState previous, boolean multiple, GsDynamicalHierarchicalNode dhnode) {
		this.state = state;
		this.previous = previous;
		this.depth = depth;
		this.multiple = multiple;
		this.dhnode = dhnode;
	}
	
	public String toString() {
		return "{state:"+state+", depth:"+depth+", multiple:"+multiple+", previous:"+previous+", dhnode:"+dhnode+"}";
	}

}
