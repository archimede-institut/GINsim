package fr.univmrs.tagc.GINsim.reg2dyn;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import fr.univmrs.tagc.GINsim.hierachicalTransitionGraph.GsHierarchicalNode;

/**
 * Used to represent the elements in the queue of the HTGSimulation
 */
public class HTGSimulationQueuedState implements HTGSimulationQueueItem  {

	/**
	 * The state itself
	 */
	private byte[] state;
	/**
	 * It's associated index. The k-th state to be discovered, will have its index = k. See Tarjan Algorithm's
	 */
	private int index;
	/**
	 * Its associated lowindex. See Tarjan Algorithm's 
	 */
	private int low_index;
	/**
	 * The set of outgoingEdges HashSet&lt;GsHierarchicalNode&gt;, that is the GsHierarchicalNode of its successors.
	 */
	public Set outgoindHNodes = null;
	/**
	 * Indicates if it is in a cycle
	 */
	private HTGSimulationQueueSCC inCycle = null;
	
	/**
	 * Simple constructor.
	 * @param state
	 * @param index
	 * @param low_index
	 * @param updater
	 */
	public HTGSimulationQueuedState(byte[] state, int index, int low_index) {
		this.setState(state);
		this.setIndex(index);
		this.setLow_index(low_index);
	}

	/* (non-Javadoc)
	 * @see fr.univmrs.tagc.GINsim.reg2dyn.HTGSimulationQueueItem#toString()
	 */
	public String toString() {
		return "["+printStateToString(getState())+", i:"+getIndex()+", li:"+getLow_index()+", out:"+outgoindHNodes+"]";
	}
	
	private static String printStateToString(byte[] t) {
		StringBuffer s = new StringBuffer();
		for (int i = 0 ; i < t.length ; i++){
			s.append(""+t[i]);
		}
		return s.toString();
	}
	
	/* (non-Javadoc)
	 * @see fr.univmrs.tagc.GINsim.reg2dyn.HTGSimulationQueueItem#getOutgoindHNodes()
	 */
	public Set getOutgoindHNodes() {
		if (outgoindHNodes == null) outgoindHNodes = new HashSet();
		return outgoindHNodes;
	}
	
	/* (non-Javadoc)
	 * @see fr.univmrs.tagc.GINsim.reg2dyn.HTGSimulationQueueItem#addOutgoingHNode(fr.univmrs.tagc.GINsim.hierachicalTransitionGraph.GsHierarchicalNode)
	 */
	public void addOutgoingHNode(GsHierarchicalNode hnode) {
		if (outgoindHNodes == null) outgoindHNodes = new HashSet();
		outgoindHNodes.add(hnode);
	}

	/* (non-Javadoc)
	 * @see fr.univmrs.tagc.GINsim.reg2dyn.HTGSimulationQueueItem#setIndex(int)
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/* (non-Javadoc)
	 * @see fr.univmrs.tagc.GINsim.reg2dyn.HTGSimulationQueueItem#getIndex()
	 */
	public int getIndex() {
		return index;
	}

	/* (non-Javadoc)
	 * @see fr.univmrs.tagc.GINsim.reg2dyn.HTGSimulationQueueItem#setLow_index(int)
	 */
	public void setLow_index(int low_index) {
		this.low_index = low_index;
	}

	/* (non-Javadoc)
	 * @see fr.univmrs.tagc.GINsim.reg2dyn.HTGSimulationQueueItem#getLow_index()
	 */
	public int getLow_index() {
		return low_index;
	}

	/* (non-Javadoc)
	 * @see fr.univmrs.tagc.GINsim.reg2dyn.HTGSimulationQueueItem#setState(byte[])
	 */
	public void setState(byte[] state) {
		this.state = state;
	}

	/* (non-Javadoc)
	 * @see fr.univmrs.tagc.GINsim.reg2dyn.HTGSimulationQueueItem#getState()
	 */
	public byte[] getState() {
		return state;
	}

	/* (non-Javadoc)
	 * @see fr.univmrs.tagc.GINsim.reg2dyn.HTGSimulationQueueItem#containsState(byte[])
	 */
	public boolean containsState(byte[] state) {
		return Arrays.equals(this.state, state);
	}

	public boolean isCycle() {
		return false;
	}

	public void setInCycle(HTGSimulationQueueSCC newCycleItem) {
		this.inCycle  = newCycleItem;
	}
	public HTGSimulationQueueSCC getInCycle() {
		return inCycle;
	}
}
