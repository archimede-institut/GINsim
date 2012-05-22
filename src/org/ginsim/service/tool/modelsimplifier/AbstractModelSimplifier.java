package org.ginsim.service.tool.modelsimplifier;

import java.util.List;
import java.util.Map;

import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;

abstract public class AbstractModelSimplifier extends Thread implements Runnable {

	
	
	/**
	 * Extract the list of required edges for a given logical function.
	 * @param m_edges
	 * @param node
	 */
	protected void extractEdgesFromNode(List<RegulatoryNode> oldNodeOrder, Map<RegulatoryNode,boolean[]> m_edges, OMDDNode node) {
		if (node.next == null) {
			return;
		}
		RegulatoryNode vertex = oldNodeOrder.get(node.level);
		boolean[] t_threshold = (boolean[])m_edges.get(vertex);
		if (t_threshold == null) {
			t_threshold = new boolean[vertex.getMaxValue()+1];
			for (int i=0 ; i<t_threshold.length ; i++) {
				t_threshold[i] = false;
			}
			m_edges.put(vertex, t_threshold);
		}

		OMDDNode child = null;
		for (int i=0 ; i<node.next.length ; i++) {
			if (child != node.next[i]) {
				if (child != null) {
					t_threshold[i] = true;
				}
				child = node.next[i];
				extractEdgesFromNode(oldNodeOrder, m_edges, node.next[i]);
			}
		}
	}

	/**
	 * Preliminary check: a node should not be self-regulated: check it
	 */
	protected void checkNoSelfReg(OMDDNode node, int level) throws GsException {
		if (node.next == null || node.level > level) {
			return;
		}
		if (node.level == level) {
			throw new GsException(GsException.GRAVITY_ERROR, "self regulated node");
		}
		for (int i=0 ; i<node.next.length ; i++) {
			checkNoSelfReg(node.next[i], level);
		}
	}

	
	/* *************************************************************
	 *  
	 *  The real algo is here
	 *  
	 *  Deleting a node means removing it (by taking into account its logical
	 *  function) from all of its targets
	 *  
	 ***************************************************************/
	/**
	 * Remove <code>regulator</code> from its target <code>node</code>.
	 * This is the first part of the algo: we have not yet found the 
	 * regulator in the logical function.
	 * It will be called recursively until we find it (or go too far)
	 * 
	 * @param node
	 * @param regulator
	 * @param level
	 * @return
	 */
	public OMDDNode remove(OMDDNode node, OMDDNode regulator, int level) throws GsException {
		if (node.next == null || node.level > level) {
			return node;
		}
		if (node.level == level) {
			if (regulator.next == null) {
				return node.next[regulator.value];
			}
			if (regulator.level == level) {
				throw new GsException(GsException.GRAVITY_ERROR, 
						"Can not continue the simplification: a circuit would get lost");
			}
			return remove(node.next, regulator);
		}
		
		OMDDNode ret = new OMDDNode();
		if (regulator.next == null || regulator.level > node.level) {
			ret.level = node.level;
			ret.next = new OMDDNode[node.next.length];
			for (int i=0 ; i<ret.next.length ; i++) {
				ret.next[i] = remove(node.next[i], regulator, level);
			}
		} else if (node.level > regulator.level) {
			ret.level = regulator.level;
			ret.next = new OMDDNode[regulator.next.length];
			for (int i=0 ; i<ret.next.length ; i++) {
				ret.next[i] = remove(node, regulator.next[i], level);
			}
		} else {
			ret.level = node.level;
			ret.next = new OMDDNode[node.next.length];
			for (int i=0 ; i<ret.next.length ; i++) {
				ret.next[i] = remove(node.next[i], regulator.next[i], level);
			}
		}
		return ret;
	}

	/**
	 * Remove <code>regulator</code> from its target <code>node</code>.
	 * This is the second part of the algo: we have found the regulator 
	 * in the logical function.
	 * We must thus follow all branches corresponding to its possible values,
	 * until we can take the final decision.
	 * 
	 * @param t_ori
	 * @param regulator
	 * @return
	 */
	public OMDDNode remove(OMDDNode[] t_ori, OMDDNode regulator) {
		if (regulator.next == null) {
			return t_ori[regulator.value];
		}
		// first, lookup for the best next step
		int best = regulator.level;
		int index = -1;
		for (int i=0 ; i<t_ori.length ; i++) {
			OMDDNode node = t_ori[i];
			if (node.next != null && node.level <= best) { 
				// also update when equal to avoid stupid optimisations...
				best = node.level;
				index = i;
			}
		}
		
		OMDDNode ret = new OMDDNode();
		ret.level = best;
		if (index == -1) {
			ret.next = new OMDDNode[regulator.next.length];
			for (int i=0 ; i<ret.next.length ; i++) {
				ret.next[i] = remove(t_ori, regulator.next[i]);
			}
		} else {
			ret.next = new OMDDNode[t_ori[index].next.length];
			for (int i=0 ; i<ret.next.length ; i++) {
				OMDDNode[] t_recur = new OMDDNode[t_ori.length];
				for (int j=0 ; j<t_recur.length ; j++) {
					OMDDNode node = t_ori[j];
					if (node.next == null || node.level > best) {
						t_recur[j] = node;
					} else {
						t_recur[j] = node.next[i];
					}
				}
				if (regulator.level == best) {
					ret.next[i] = remove(t_recur, regulator.next[i]);
				} else {
					ret.next[i] = remove(t_recur, regulator);
				}
			}
		}
		return ret;
	}
	
}

