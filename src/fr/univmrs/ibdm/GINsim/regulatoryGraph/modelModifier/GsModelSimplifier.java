package fr.univmrs.ibdm.GINsim.regulatoryGraph.modelModifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsGraphManager;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsGenericRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.OmddNode;

/**
 * Build a simplified model, based on a complete one, by removing some nodes.
 */
public class GsModelSimplifier implements GsGenericRegulatoryGraph {

	GsGraphManager manager;
	int[] t_remove = null;
	
	GsGenericRegulatoryGraph graph;
	
	Vector oldNodeOrder;
	Vector nodeOrder;
	OmddNode[] t_oldMDD;
	OmddNode[] t_MDD;
	Map m_MDD4node;
	Map m_inconsistencies;
	
	public GsModelSimplifier() {
	}
	
	public GsModelSimplifier(GsGenericRegulatoryGraph graph) {
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

	public Vector getNodeOrderForSimulation() {
		if (oldNodeOrder == null) {
			return null;
		}
		if (t_remove == null) {
			return oldNodeOrder;
		}
		return null;
	}

	public OmddNode[] getParametersForSimulation(boolean focal) {
		if (oldNodeOrder == null) {
			return null;
		}
		if (t_remove == null) {
			return graph.getParametersForSimulation(focal);
		}
		t_oldMDD = graph.getParametersForSimulation(focal);
		
		return t_MDD;
	}
}
