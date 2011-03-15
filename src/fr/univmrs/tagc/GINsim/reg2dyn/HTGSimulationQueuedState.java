package fr.univmrs.tagc.GINsim.reg2dyn;

import java.util.HashSet;
import java.util.Set;

import fr.univmrs.tagc.GINsim.hierachicalTransitionGraph.GsHierarchicalNode;

public class HTGSimulationQueuedState {

	public byte[] state;
	public int index;
	public int low_index;
	public SimulationUpdater updater;
	public Set outgoindHNodes = null;
	
	public HTGSimulationQueuedState(byte[] state, int index, int low_index, SimulationUpdater updater) {
		this.state = state;
		this.index = index;
		this.low_index = low_index;
		this.updater = updater;
	}

	public String toString() {
		return "["+print_state(state)+", i:"+index+", li:"+low_index+", out:"+outgoindHNodes+"]";
	}
	
	public static String print_state(byte[] t) {
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
	
	public void addOutgoingHNode(GsHierarchicalNode hnode) {
		if (outgoindHNodes == null) outgoindHNodes = new HashSet();
		outgoindHNodes.add(hnode);
	}
}
