package fr.univmrs.ibdm.GINsim.regulatoryGraph.modelModifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsGraphManager;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsGenericRegulatoryGraph;
import fr.univmrs.tagc.mdd.DecisionDiagramInfo;
import fr.univmrs.tagc.mdd.MDDLeaf;
import fr.univmrs.tagc.mdd.MDDNode;
import fr.univmrs.tagc.mdd.MDDVarNode;

/**
 * Build a simplified model, based on a complete one, by removing some nodes.
 */
public class ModelSimplifier {

	GsGraphManager manager;
	int[] t_remove = null;

	GsGenericRegulatoryGraph graph;
	DecisionDiagramInfo ddi;

	Vector oldNodeOrder;
	Vector nodeOrder;
	MDDNode[] t_oldMDD;
	MDDNode[] t_MDD;
	Map m_MDD4node;
	Map m_inconsistencies;

	public ModelSimplifier() {
	}

	public ModelSimplifier(GsGenericRegulatoryGraph graph) {
		this.graph = graph;
		if (graph != null) {
			oldNodeOrder = graph.getNodeOrderForSimulation();
		}
		if (graph instanceof GsGraph) {
			manager = ((GsGraph)graph).getGraphManager();
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

	private void removeNode(int index) {

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

	public MDDNode simplify(MDDNode ori, MDDNode deleted, int level) {
		int oOrder, dOrder;
		if (ori instanceof MDDLeaf) {
			oOrder = Integer.MAX_VALUE;
		} else {
			oOrder = ((MDDVarNode)ori).vinfo.order;
		}
		if (deleted instanceof MDDLeaf) {
			dOrder = Integer.MAX_VALUE;
		} else {
			dOrder = ((MDDVarNode)deleted).vinfo.order;
		}
		if (oOrder > level) {
			return ori;
		}

		int newOrder;
		MDDNode[] next;
		if (dOrder > oOrder) {
			if (oOrder == level) {
				if (deleted instanceof MDDLeaf) {
					return ((MDDVarNode)ori).next[((MDDLeaf)deleted).value];
				}
				return replace(((MDDVarNode)ori).next, deleted);
			}
			newOrder = oOrder;
			next = new MDDNode[((MDDVarNode)ori).next.length];
			for (int i=0 ; i<next.length ; i++) {
				next[i] = simplify(((MDDVarNode)ori).next[i], deleted, level);
			}
		} else if (dOrder == oOrder) {
			if (dOrder == level) {
				addInconsistency();
				return replace((MDDVarNode)ori, (MDDVarNode)deleted);
			}
			newOrder = oOrder;
			next = new MDDNode[((MDDVarNode)ori).next.length];
			for (int i=0 ; i<next.length ; i++) {
				next[i] = simplify(((MDDVarNode)ori).next[i], ((MDDVarNode)deleted).next[i], level);
			}
		} else {
			if (dOrder == level) { // should have returned ori.clone()
				// no inconsistency <b>for THIS target</b>
				System.out.println("DEBUG: mofifier: deleted.level == level < ori.level");
				return null;
			}
			newOrder = dOrder;
			next = new MDDNode[((MDDVarNode)deleted).next.length];
			for (int i=0 ; i<next.length ; i++) {
				next[i] = simplify(ori, ((MDDVarNode)deleted).next[i], level);
			}
		}
		return ddi.getNewNode(newOrder, next);
	}

	/**
	 * the node was found, but it is self-regulated, do something here!
	 *
	 * @param ori
	 * @param deleted
	 * @return
	 */
	private MDDNode replace(MDDVarNode ori, MDDVarNode deleted) {
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
	private MDDNode replace(MDDNode[] ori, MDDNode deleted) {
		// if the deleted node has an assigned value, we are done
		if (deleted instanceof MDDLeaf) {
			return ori[((MDDLeaf)deleted).value];
		}

		MDDVarNode vdeleted = (MDDVarNode)deleted;

		// find the first level
		int level = vdeleted.vinfo.order;
		int nbnext = vdeleted.next.length;
		for (int i=0 ; i<ori.length ; i++) {
			if (ori[i] instanceof MDDVarNode && ((MDDVarNode)ori[i]).vinfo.order < level) {
				level = ((MDDVarNode)ori[i]).vinfo.order;
				nbnext = ((MDDVarNode)ori[i]).next.length;
			}
		}

		// build the result
		MDDNode[] t_next = new MDDNode[ori.length];
		boolean[] t_visit = new boolean[ori.length];
		for (int i=0 ; i<t_next.length ; i++) {
			if (ori[i] instanceof MDDLeaf || ((MDDVarNode)ori[i]).vinfo.order > level) {
				t_visit[i] = false;
				t_next[i] = ori[i];
			} else {
				t_visit[i] = true;
			}
		}
		MDDNode del_next = deleted;
		MDDNode[] next = new MDDNode[nbnext];
		for (int n=0 ; n<nbnext ; n++) {
			// find out which nodes must be kept and which must be visited
			if (vdeleted.vinfo.order == level) {
				del_next = vdeleted.next[n];
			}
			for (int i=0 ; i<t_visit.length ; i++) {
				if (t_visit[i]) {
					t_next[i] = ((MDDVarNode)ori[i]).next[n];
				}
			}
			next[n] = replace(t_next, del_next);
		}
		return ddi.getNewNode(level, next);
	}

//	public Vector getNodeOrderForSimulation() {
//		if (oldNodeOrder == null) {
//			return null;
//		}
//		if (t_remove == null) {
//			return oldNodeOrder;
//		}
//		return null;
//	}
//
//	public OmddNode[] getParametersForSimulation(boolean focal) {
//		if (oldNodeOrder == null) {
//			return null;
//		}
//		if (t_remove == null) {
//			return graph.getParametersForSimulation(focal);
//		}
//		t_oldMDD = graph.getParametersForSimulation(focal);
//
//		return t_MDD;
//	}
}
