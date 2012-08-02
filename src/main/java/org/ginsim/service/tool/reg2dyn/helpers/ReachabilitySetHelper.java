package org.ginsim.service.tool.reg2dyn.helpers;

import java.util.List;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.NodeInfo;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.dynamicgraph.DynamicNode;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;
import org.ginsim.service.tool.reg2dyn.SimulationParameters;
import org.ginsim.service.tool.reg2dyn.SimulationQueuedState;


public class ReachabilitySetHelper implements SimulationHelper {
	protected DynamicNode node;
	protected int[] t_max;
	protected int length;
	protected OMDDNode dd_reachable = OMDDNode.TERMINALS[0];
	
	ReachabilitySetHelper(LogicalModel model, SimulationParameters params) {
		List<NodeInfo> nodes = model.getNodeOrder();
		length = nodes.size();
		t_max = new int[length];
		for (int i=0 ; i<length ; i++) {
			t_max[i] = (nodes.get(i)).getMax()+1;
		}
	}
	
	public boolean addNode(SimulationQueuedState item) {
		
		OMDDNode newReachable = addReachable(dd_reachable, item.state, 0);
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
	
	protected OMDDNode addReachable(OMDDNode reachable, byte[] vstate, int depth) {
		if (depth == vstate.length) {
			if (reachable.equals(OMDDNode.TERMINALS[1])) {
				return null;
			}
			return OMDDNode.TERMINALS[1];
		}
		byte curval = vstate[depth];
		if (reachable.next == null) {
			if (reachable.value == 1) {
				return null;
			}
			OMDDNode ret = new OMDDNode();
			ret.level = depth;
			ret.next = new OMDDNode[t_max[depth]];
			for (int i=0 ; i<ret.next.length ; i++) {
				if (i==curval) {
					ret.next[i] = addReachable(reachable, vstate, depth+1);
				} else {
					ret.next[i] = OMDDNode.TERMINALS[0];
				}
			}
			return ret;
		}
		// reachable is not a leaf: first explore it and then create a new node if needed
		OMDDNode child;
		if (reachable.level > depth) {
			child = addReachable(reachable, vstate, depth+1);
		} else {
			child = addReachable(reachable.next[curval], vstate, depth+1);
		}
		if (child != null) {
			OMDDNode ret = new OMDDNode();
			ret.level = depth;
			ret.next = new OMDDNode[t_max[depth]];
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
	
	public Graph getDynamicGraph() {
		return null;
	}
	
	public void setNode(Object node) {
		this.node = (DynamicNode) node;
	}
}