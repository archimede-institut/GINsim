package fr.univmrs.ibdm.GINsim.regulatoryGraph.modelModifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.graph.GsGraphManager;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.OmddNode;

/**
 * Build a simplified model, based on a complete one, by removing some nodes.
 */
public class ModelSimplifier {

	GsGraphManager manager;
	int[] t_remove = null;

	GsRegulatoryGraph graph;

	Vector oldNodeOrder;
	Vector nodeOrder;
	OmddNode[] t_oldMDD;
	OmddNode[] t_MDD;
	Map m_MDD4node;
	Map m_inconsistencies;

	public ModelSimplifier() {
	}

	public ModelSimplifier(GsRegulatoryGraph graph) {
		this.graph = graph;
		if (graph != null) {
			oldNodeOrder = graph.getNodeOrderForSimulation();
			manager = graph.getGraphManager();
		}
	}

	public void setRemoveList(Vector v_remove) {
		if (oldNodeOrder != null && v_remove != null) {
			if (m_inconsistencies != null) {
				m_inconsistencies.clear();
			} else {
				m_inconsistencies = new HashMap();
			}
			this.t_remove = new int[v_remove.size()];
			for (int i=0 ; i<t_remove.length ; i++) {
				t_remove[i] = oldNodeOrder.indexOf(v_remove.get(i));
			}
			nodeOrder = new Vector();
			for (int i=0 ; i<oldNodeOrder.size() ; i++) {
				Object node = oldNodeOrder.get(i);
				if (v_remove.contains(node)) {
					nodeOrder.add(node);
				}
			}
		}
	}

	private void addInconsistency() {
		// TODO: better reporting of inconsistencies
		if (m_inconsistencies == null) {
			m_inconsistencies = new HashMap();
		}
		m_inconsistencies.put(null, null);
		System.out.println("DEBUG: modifier: inconsistency introduced");
	}

	public boolean isInconsistent() {
		return m_inconsistencies != null && m_inconsistencies.size() > 0;
	}

	public OmddNode simplify(OmddNode ori, OmddNode deleted, int level) {
		if (ori.next == null || ori.level > level) {
			return (OmddNode)ori.clone();
		}

		OmddNode ret;
		if (deleted.next == null || deleted.level > ori.level) {
			if (ori.level == level) {
				if (deleted.next == null) {
					return (OmddNode)ori.next[deleted.value].clone();
				}
				return replace(ori.next, deleted);
			}
			ret = new OmddNode();
			ret.level = ori.level;
			ret.next = new OmddNode[ori.next.length];
			for (int i=0 ; i<ret.next.length ; i++) {
				ret.next[i] = simplify(ori.next[i], deleted, level);
			}
		} else if (deleted.level == ori.level) {
			if (ori.level == level) {
				addInconsistency();
				return replace(ori, deleted);
			}
			ret = new OmddNode();
			ret.level = ori.level;
			ret.next = new OmddNode[ori.next.length];
			for (int i=0 ; i<ret.next.length ; i++) {
				ret.next[i] = simplify(ori.next[i], deleted.next[i], level);
			}
		} else {
			if (deleted.level == level) { // should have returned ori.clone()
				// no inconsistency <b>for THIS target</b>
				System.out.println("DEBUG: mofifier: deleted.level == level < ori.level");
				return null;
			}
			ret = new OmddNode();
			ret.level = deleted.level;
			ret.next = new OmddNode[deleted.next.length];
			for (int i=0 ; i<ret.next.length ; i++) {
				ret.next[i] = simplify(ori, deleted.next[i], level);
			}
		}
		return ret;
	}

	/**
	 * the node was found, but it is self-regulated, do something here!
	 *
	 * @param ori
	 * @param deleted
	 * @return
	 */
	private OmddNode replace(OmddNode ori, OmddNode deleted) {
		// FIXME: warning and some smart stuff needed here!
		return replace(ori.next, deleted.next[0]);
	}

	/**
	 * need to follow ALL children of the "replaced" node!
	 * the final value will be selected upon it, but HOW ?
	 *
	 * @param ori
	 * @param deleted
	 * @return
	 */
	private OmddNode replace(OmddNode[] ori, OmddNode deleted) {
		// if the deleted node has an assigned value, we are done
		if (deleted.next == null) {
			return (OmddNode)ori[deleted.value].clone();
		}

		// find the first level
		int level = deleted.level;
		int nbnext = deleted.next.length;
		for (int i=0 ; i<ori.length ; i++) {
			if (ori[i].next != null && ori[i].level < level) {
				level = ori[i].level;
				nbnext = ori[i].next.length;
			}
		}

		// build the result
		OmddNode[] t_next = new OmddNode[ori.length];
		boolean[] t_visit = new boolean[ori.length];
		for (int i=0 ; i<t_next.length ; i++) {
			if (ori[i].next == null || ori[i].level > level) {
				t_visit[i] = false;
				t_next[i] = ori[i];
			} else {
				t_visit[i] = true;
			}
		}
		OmddNode del_next = deleted;
		OmddNode ret = new OmddNode();
		ret.level = level;
		ret.next = new OmddNode[nbnext];
		for (int n=0 ; n<nbnext ; n++) {
			// find out which nodes must be kept and which must be visited
			if (deleted.level == level) {
				del_next = deleted.next[n];
			}
			for (int i=0 ; i<t_visit.length ; i++) {
				if (t_visit[i]) {
					t_next[i] = ori[i].next[n];
				}
			}
			ret.next[n] = replace(t_next, del_next);
		}
		return ret;
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
	public OmddNode remove(OmddNode node, OmddNode regulator, int level) throws GsException {
		
		if (node.next == null || node.level > level) {
			return node;
		}
		if (node.level == level) {
			if (regulator.next == null) {
				return node.next[regulator.value];
			}
			if (regulator.level == level) {
				throw new GsException(GsException.GRAVITY_ERROR, "Can not continue the simplification: a circuit would get lost");
			}
			return remove(node.next, regulator);
		}
		
		OmddNode ret = new OmddNode();
		if (regulator.next == null || regulator.level > node.level) {
			ret.level = node.level;
			ret.next = new OmddNode[node.next.length];
			for (int i=0 ; i<ret.next.length ; i++) {
				ret.next[i] = remove(node.next[i], regulator, level);
			}
		} else if (node.level > regulator.level) {
			ret.level = regulator.level;
			ret.next = new OmddNode[regulator.next.length];
			for (int i=0 ; i<ret.next.length ; i++) {
				ret.next[i] = remove(node, regulator.next[i], level);
			}
		} else {
			ret.level = node.level;
			ret.next = new OmddNode[node.next.length];
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
	public OmddNode remove(OmddNode[] t_ori, OmddNode regulator) {
		if (regulator.next == null) {
			return t_ori[regulator.value];
		}
		// first, lookup for the best next step
		int best = regulator.level;
		int index = -1;
		for (int i=0 ; i<t_ori.length ; i++) {
			OmddNode node = t_ori[i];
			if (node.next != null && node.level <= best) { 
				// also update when equal to avoid stupid optimisations...
				best = node.level;
				index = i;
			}
		}
		
		OmddNode ret = new OmddNode();
		ret.level = best;
		if (index == -1) {
			ret.next = new OmddNode[regulator.next.length];
			for (int i=0 ; i<ret.next.length ; i++) {
				ret.next[i] = remove(t_ori, regulator.next[i]);
			}
		} else {
			ret.next = new OmddNode[t_ori[index].next.length];
			for (int i=0 ; i<ret.next.length ; i++) {
				OmddNode[] t_recur = new OmddNode[t_ori.length];
				for (int j=0 ; j<t_recur.length ; j++) {
					OmddNode node = t_ori[j];
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
