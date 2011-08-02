package fr.univmrs.tagc.GINsim.reg2dyn;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import fr.univmrs.tagc.GINsim.hierachicalTransitionGraph.GsHierarchicalNode;
import fr.univmrs.tagc.GINsim.hierachicalTransitionGraph.GsHierarchicalNodeSet;

/**
 * Used to represent the elements in the queue of the HTGSimulation
 */
public class HTGSimulationQueueState implements HTGSimulationQueueItem  {

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
	public HTGSimulationQueueState(byte[] state, int index, int low_index) {
		this.setState(state);
		this.setIndex(index);
		this.setLow_index(low_index);
	}

	/* (non-Javadoc)
	 * @see fr.univmrs.tagc.GINsim.reg2dyn.HTGSimulationQueueItem#toString()
	 */
	public String toString() {
		return "["+printStateToString(getState())+", i:"+getIndex()+", li:"+getLow_index()+"]";
	}
	
	private static String printStateToString(byte[] t) {
		StringBuffer s = new StringBuffer();
		for (int i = 0 ; i < t.length ; i++){
			s.append(""+t[i]);
		}
		return s.toString();
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

	/**
	 * Set the SCC that contains the state. 
	 * 
	 * Note it could be a discarded SCC after a merge.
	 * 
	 * @param newCycleItem
	 */
	public void setInCycle(HTGSimulationQueueSCC newCycleItem) {
		this.inCycle  = newCycleItem;
	}
	/**
	 * Retrieve the real SCC containing the state.
	 * @param nodeSet
	 * @param queue
	 * @return
	 */
	public GsHierarchicalNode getInCycle(GsHierarchicalNodeSet nodeSet, List queue) {
		if (inCycle == null) return null;
		for (Iterator it = nodeSet.iterator(); it.hasNext();) {
			GsHierarchicalNode n = (GsHierarchicalNode) it.next();
			if (n.contains(state)) return n;
		}
		for (ListIterator it = queue.listIterator(queue.size()); it.hasPrevious();) {
			HTGSimulationQueueItem e = (HTGSimulationQueueItem) it.previous();
			if (e.isCycle() && e.containsState(state)) return ((HTGSimulationQueueSCC) e).getSCC();
		}
		return inCycle.getSCC();
	}
}
