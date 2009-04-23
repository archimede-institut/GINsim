package fr.univmrs.tagc.GINsim.reg2dyn;

import java.util.LinkedList;
import java.util.List;

import fr.univmrs.tagc.GINsim.dynamicalHierachicalGraph.GsDynamicalHierarchicalNode;
import fr.univmrs.tagc.common.GsException;

public class DynamicalHierarchicalSimulationQueuedState {
	public short[] state;
	public long state_l = -1;
	public DynamicalHierarchicalSimulationQueuedState previous = null;
	public int depth;
	public List childs = null;
	public int totalChild = -1;
	private int processedChilds;

	public DynamicalHierarchicalSimulationQueuedState(short[] state, int depth, DynamicalHierarchicalSimulationQueuedState previous) {
		this.state = state;
		this.depth = depth;
		this.previous = previous;
		processedChilds = 0;
	}
	
	public String toString() {
		return "{state:"+state_l+"="+DynamicalHierarchicalSimulation.print_t(state)+", depth:"+depth+", previous:"+(previous!=null?""+previous.depth:"None")+", childs"+processedChilds+"/"+totalChild+"}";
	}
	
	public boolean equals(Object other) {
		return equals((DynamicalHierarchicalSimulationQueuedState)other);
	}
	
	public boolean equals(DynamicalHierarchicalSimulationQueuedState other) {
		if (state_l < 0) { //if state has not been hashed yet, we hash it.
			hash();
		}
		if (other.state_l < 0) { //if state has not been hashed yet, we hash it.
			other.hash();
		}
		if (this.state_l != other.state_l) { //If hash are different, then the states are different.
			return false;
		}
		for (int i = 0; i < state.length; i++) {//else compare the states manually
			if (this.state[i] != other.state[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Append 'dhnode' to the parent child list of 'this'. If the child list doesn't exists, it creates it.
	 * @param dhnode a child to append.
	 */
	public void setParentNextChild(GsDynamicalHierarchicalNode node) {
		if (previous != null) {
			if (previous.childs == null) {
				previous.childs = new LinkedList();
			}
			previous.childs.add(node);
		}
	}
	
	/**
	 * A surjective function giving an unique long for each state array
	 */
	public int hashCode() {
		int l = 1;
		for (int i = 0; i < state.length; i++) {
			l += state[i];
			l <<= 3;
		}
		return l;
	}
	/**
	 * A surjective function giving an unique long for each state array
	 */
	public void hash() {
		long l = 1;
		for (int i = 0; i < state.length; i++) {
			l += state[i];
			l <<= 3;
		}
		state_l = Math.abs(l);
	}

	public void tellParentOneChildIsProcess(int processedChilds) {
		if (previous != null) {
			previous.processedChilds += processedChilds;
		}
	}
	
	public boolean isProcessed() throws GsException {
		if ( processedChilds > totalChild && totalChild != -1) {
			throw new GsException(1, "Error  processedChilds > totalChild : "+this);
		}
		return processedChilds == totalChild;
	}
}
