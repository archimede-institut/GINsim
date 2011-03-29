package fr.univmrs.tagc.GINsim.reg2dyn;

import java.util.HashSet;
import java.util.Set;

import fr.univmrs.tagc.GINsim.hierachicalTransitionGraph.GsHierarchicalNode;

/**
 * Used to represent the elements in the queue of the HTGSimulation
 */
public class HTGSimulationQueuedState {

	/**
	 * The state itself
	 */
	public byte[] state;
	/**
	 * It's associated index. The k-th state to be discovered, will have its index = k. See Tarjan Algorithm's
	 */
	public int index;
	/**
	 * Its associated lowindex. See Tarjan Algorithm's 
	 */
	public int low_index;
	/**
	 * The updater (iterator) returning the successor of the state
	 */
	public SimulationUpdater updater;
	/**
	 * The set of outgoingEdges HashSet&lt;GsHierarchicalNode&gt;, that is the GsHierarchicalNode of its successors.
	 */
	public Set outgoindHNodes = null;
	
	/**
	 * Simple constructor.
	 * @param state
	 * @param index
	 * @param low_index
	 * @param updater
	 */
	public HTGSimulationQueuedState(byte[] state, int index, int low_index, SimulationUpdater updater) {
		this.state = state;
		this.index = index;
		this.low_index = low_index;
		this.updater = updater;
	}

	public String toString() {
		return "["+printStateToString(state)+", i:"+index+", li:"+low_index+", out:"+outgoindHNodes+"]";
	}
	
	private static String printStateToString(byte[] t) {
		StringBuffer s = new StringBuffer();
		for (int i = 0 ; i < t.length ; i++){
			s.append(""+t[i]);
		}
		return s.toString();
	}
	
	public Set getOutgoindHNodes() {
		if (outgoindHNodes == null) outgoindHNodes = new HashSet();
		return outgoindHNodes;
	}
	
	/**
	 * Add hnode to the set of outgoingHNodes. Initialize it if it isn't initialized yet.
	 * @param hnode the GsHierarchicalNode to add to the set
	 */
	public void addOutgoingHNode(GsHierarchicalNode hnode) {
		if (outgoindHNodes == null) outgoindHNodes = new HashSet();
		outgoindHNodes.add(hnode);
	}
}
