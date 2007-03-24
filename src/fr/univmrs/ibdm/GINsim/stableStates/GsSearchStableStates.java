package fr.univmrs.ibdm.GINsim.stableStates;

import java.util.Vector;

import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.OmddNode;

public class GsSearchStableStates {

	private GsRegulatoryGraph regGraph;
	Vector nodeOrder;
	OmddNode[] t_param;
	OmddNode dd_stable;

	GsSearchStableStates(GsGraph regGraph) {
		this.regGraph = (GsRegulatoryGraph)regGraph;
		this.nodeOrder = regGraph.getNodeOrder();
	}
	
	public void run() {
		t_param = regGraph.getAllTrees(true);
		
		long start = System.currentTimeMillis();
		dd_stable = OmddNode.TERMINALS[1];
		for (int i=0 ; i<t_param.length ; i++) {
			if (i%10 == 0) {
				System.out.println("  "+i);
			}
			dd_stable = buildStableConditionFromParam(i, 
					((GsRegulatoryVertex)nodeOrder.get(i)).getMaxValue()+1,
					t_param[i],
					dd_stable).reduce();
		}
		System.out.println("stable states search: "+(System.currentTimeMillis()-start)+"ms");
		showStableState(dd_stable);
	}
	
	
	
	// "stupid" method
	private OmddNode buildStableConditionFromParam(int order, int nbChild, OmddNode param) {
		if (param.next == null || param.level > order) {
			OmddNode stable = new OmddNode();
			stable.level = order;
			stable.next = new OmddNode[nbChild];
			for (int i=0 ; i<stable.next.length ; i++) {
				stable.next[i] = isStable(i, param);
			}
			return stable;
		}
		if (param.level == order) {
			OmddNode stable = new OmddNode();
			stable.level = order;
			stable.next = new OmddNode[nbChild];
			for (int i=0 ; i<stable.next.length ; i++) {
				stable.next[i] = isStable(i, param.next[i]);
			}
			return stable;
		}
		OmddNode stable = new OmddNode();
		stable.level = param.level;
		stable.next = new OmddNode[param.next.length];
		for (int i=0 ; i<stable.next.length ; i++) {
			stable.next[i] = buildStableConditionFromParam(order, nbChild, param.next[i]);
		}
		return stable;
	}
	private OmddNode isStable(int value, OmddNode param) {
		if (param.next == null) {
			if (param.value == value) {
				return OmddNode.TERMINALS[1];
			}
			return OmddNode.TERMINALS[0];
		}
		OmddNode stable = new OmddNode();
		stable.level = param.level;
		stable.next = new OmddNode[param.next.length];
		for (int i=0 ; i<stable.next.length ; i++) {
			stable.next[i] = isStable(value, param.next[i]);
		}
		return stable;
	}


	// smarter method
	private OmddNode buildStableConditionFromParam(int order, int nbChild, OmddNode param, OmddNode known) {
		if (known.next == null) {
			if (known.value == 0) {
				return OmddNode.TERMINALS[0];
			}
			return buildStableConditionFromParam(order, nbChild, param);
		}

		OmddNode stable = new OmddNode();
		if ((param.next == null || param.level > order) && known.level > order) {
			stable.level = order;
			stable.next = new OmddNode[nbChild];
			for (int i=0 ; i<stable.next.length ; i++) {
				stable.next[i] = isStable(i, param, known);
			}
		} else if (param.next == null || param.level > known.level) {
			stable.level = known.level;
			stable.next = new OmddNode[known.next.length];
			if (stable.level == order) {
				for (int i=0 ; i<stable.next.length ; i++) {
 					stable.next[i] = isStable(i, param, known.next[i]);
				}
			} else {
				for (int i=0 ; i<stable.next.length ; i++) {
					stable.next[i] = buildStableConditionFromParam(order, nbChild, param, known.next[i]);
				}
			}
		} else if (param.level < known.level) {
			stable.level = param.level;
			stable.next = new OmddNode[param.next.length];
			if (stable.level == order) {
				for (int i=0 ; i<stable.next.length ; i++) {
 					stable.next[i] = isStable(i, param.next[i], known);
				}
			} else {
				for (int i=0 ; i<stable.next.length ; i++) {
					stable.next[i] = buildStableConditionFromParam(order, nbChild, param.next[i], known);
				}
			}
		} else {
			// param.level = known.level
			stable.level = param.level;
			stable.next = new OmddNode[param.next.length];
			if (stable.level == order) {
				for (int i=0 ; i<stable.next.length ; i++) {
					stable.next[i] = isStable(i, param.next[i], known.next[i]);
				}
			} else {
				for (int i=0 ; i<stable.next.length ; i++) {
					stable.next[i] = buildStableConditionFromParam(order, nbChild, param.next[i], known.next[i]);
				}
			}
		}
		return stable;
	}
	
	private OmddNode isStable(int value, OmddNode param, OmddNode known) {
		if (known.next == null) {
			if (known.value == 0) {
				return OmddNode.TERMINALS[0];
			}
			return isStable(value, param);
		}
		if (param.next == null) {
			if (param.value == value) {
				return known;
			}
			return OmddNode.TERMINALS[0];
		}
		
		if (param.level < known.level) {
			OmddNode stable = new OmddNode();
			stable.level = param.level;
			stable.next = new OmddNode[param.next.length];
			for (int i=0 ; i<stable.next.length ; i++) {
				stable.next[i] = isStable(value, param.next[i], known);
			}
			return stable;
		}
		if (param.level > known.level) {
			OmddNode stable = new OmddNode();
			stable.level = known.level;
			stable.next = new OmddNode[known.next.length];
			for (int i=0 ; i<stable.next.length ; i++) {
				stable.next[i] = isStable(value, param, known.next[i]);
			}
			return stable;
		}
		// param.level == known.level
		OmddNode stable = new OmddNode();
		stable.level = param.level;
		stable.next = new OmddNode[param.next.length];
		for (int i=0 ; i<stable.next.length ; i++) {
			stable.next[i] = isStable(value, param.next[i], known.next[i]);
		}
		return stable;
	}
	
	
	// show stable state
	private void showStableState (OmddNode stable) {
		int[] state = new int[nodeOrder.size()];
		for (int i=0 ; i<state.length ; i++) {
			state[i] = -1;
		}
		findStableState(state, stable);
	}
	
	private void findStableState(int[] state, OmddNode stable) {
		if (stable.next == null) {
			if (stable.value == 1) {
				// we have a stable state:
				System.out.print("stable: ");
				for (int i=0 ; i<state.length ; i++) {
					System.out.print(state[i]+" ");
				}
				System.out.println();
			}
			return;
		}
		for (int i=0 ; i<stable.next.length ; i++) {
			state[stable.level] = i;
			findStableState(state, stable.next[i]);
		}
		state[stable.level] = -1;
	}
}
