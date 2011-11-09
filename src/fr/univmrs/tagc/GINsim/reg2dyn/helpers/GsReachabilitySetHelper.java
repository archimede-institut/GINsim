package fr.univmrs.tagc.GINsim.reg2dyn.helpers;

import org.ginsim.graph.Graph;

import fr.univmrs.tagc.GINsim.dynamicGraph.GsDynamicNode;

import fr.univmrs.tagc.GINsim.reg2dyn.GsSimulationParameters;
import fr.univmrs.tagc.GINsim.reg2dyn.SimulationQueuedState;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;

public class GsReachabilitySetHelper extends SimulationHelper {
	protected GsDynamicNode node;
	protected int[] t_max;
	protected int length;
	protected OmddNode dd_reachable = OmddNode.TERMINALS[0];
	
	GsReachabilitySetHelper(GsSimulationParameters params) {
		length = params.nodeOrder.size();
		t_max = new int[length];
		for (int i=0 ; i<length ; i++) {
			t_max[i] = ((GsRegulatoryVertex)params.nodeOrder.get(i)).getMaxValue()+1;
		}
	}
	
	public boolean addNode(SimulationQueuedState item) {
		
		OmddNode newReachable = addReachable(dd_reachable, item.state, 0);
		if (newReachable != null) {
			dd_reachable = newReachable.reduce();
			return true;
		}
		return false;
	}

	public Graph endSimulation() {
//		System.out.println("results ("+nbnode+" nodes):");
//		dd_stable = dd_stable.reduce();
//		dd_reachable = dd_reachable.reduce();
//		System.out.println("-------- STABLES -----------");
//		System.out.println(dd_stable.getString(0, params.nodeOrder));
//		System.out.println("-------- REACHABLE ----------");
//		System.out.println(dd_reachable.getString(0, params.nodeOrder));
//		System.out.println("----------------------------");
		return null;
	}

	public void setStable() {
	}
	
	protected OmddNode addReachable(OmddNode reachable, byte[] vstate, int depth) {
		if (depth == vstate.length) {
			if (reachable.equals(OmddNode.TERMINALS[1])) {
				return null;
			}
			return OmddNode.TERMINALS[1];
		}
		byte curval = vstate[depth];
		if (reachable.next == null) {
			if (reachable.value == 1) {
				return null;
			}
			OmddNode ret = new OmddNode();
			ret.level = depth;
			ret.next = new OmddNode[t_max[depth]];
			for (int i=0 ; i<ret.next.length ; i++) {
				if (i==curval) {
					ret.next[i] = addReachable(reachable, vstate, depth+1);
				} else {
					ret.next[i] = OmddNode.TERMINALS[0];
				}
			}
			return ret;
		}
		// reachable is not a leaf: first explore it and then create a new node if needed
		OmddNode child;
		if (reachable.level > depth) {
			child = addReachable(reachable, vstate, depth+1);
		} else {
			child = addReachable(reachable.next[curval], vstate, depth+1);
		}
		if (child != null) {
			OmddNode ret = new OmddNode();
			ret.level = depth;
			ret.next = new OmddNode[t_max[depth]];
			for (int i=0 ; i<ret.next.length ; i++) {
				if (i==curval) {
					ret.next[i] = child;
				} else {
					ret.next[i] = reachable.level > depth ? reachable : reachable.next[i];
				}
			}
			return ret;
		}
		return null;
	}
	
	public Object getNode() {
		return node;
	}
	
	public Graph getRegulatoryGraph() {
		return null;
	}
	
	public Graph getDynamicGraph() {
		return null;
	}
	
	public void setNode(Object node) {
		this.node = (GsDynamicNode) node;
	}
}