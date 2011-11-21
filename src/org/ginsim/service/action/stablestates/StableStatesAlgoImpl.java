package org.ginsim.service.action.stablestates;

import java.util.Iterator;
import java.util.List;

import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.graph.regulatorygraph.RegulatoryVertex;
import org.ginsim.graph.regulatorygraph.mutant.Perturbation;
import org.ginsim.graph.regulatorygraph.omdd.OMDDNode;


/**
 * This implements an analytic search of stable states. A state "x" is stable if, for every gene "i",
 * K(x) = x(i).
 * 
 * To find a stable state, one can build a MDD for each gene, giving the context under which 
 * THIS gene is stable.Then the stable states can be found by combining these diagrams.
 * 
 * To improve performances, the individuals "stability" MDD are not built independently 
 * but immediately assembled.
 * The order in which they are considerd is also chosen to keep them small as long as possible.
 */
public class StableStatesAlgoImpl implements StableStateSearcher {

	private final RegulatoryGraph regGraph;
	List nodeOrder;
	OMDDNode[] t_param;
	OMDDNode dd_stable;
	Perturbation mutant;
	boolean[][] t_reg;
	int[][] t_newreg;
	
	/** use a reordering to improve the size of the MDD ? */
	static final boolean ORDERTEST = true;
	
	int bestIndex, bestValue;
	int nbgene, nbremain;
	
	public StableStatesAlgoImpl( RegulatoryGraph regGraph) {
		
		this.regGraph = regGraph;
		this.nodeOrder = regGraph.getNodeOrder();
	}

	@Override
	public void setPerturbation( Perturbation mutant) {
		this.mutant = mutant;
	}
	
	@Override
	public void setNodeOrder(List<RegulatoryVertex> sortedVars, OMDDNode[] tReordered) {
		throw new RuntimeException("Custom node order in stable state search needs love");
	}
	
	@Override
	public OMDDNode getStables() {
		if (ORDERTEST) {
			buildAdjTable();
		}
		
		this.t_param = regGraph.getAllTrees(true);
		if (mutant != null) {
			mutant.apply(t_param, regGraph);
		}
		
		dd_stable = OMDDNode.TERMINALS[1];
		for (int i=0 ; i<t_param.length ; i++) {
			if (ORDERTEST) {
				int sel = selectNext();
				dd_stable = buildStableConditionFromParam(sel, 
						((RegulatoryVertex)nodeOrder.get(sel)).getMaxValue()+1,
						t_param[sel],
						dd_stable).reduce();
			} else {
				if (i%10 == 0) {
					System.out.println("  "+i);
				}
				dd_stable = buildStableConditionFromParam(i, 
						((RegulatoryVertex)nodeOrder.get(i)).getMaxValue()+1,
						t_param[i],
						dd_stable).reduce();
			}
		}
		return dd_stable;
	}

	private void buildAdjTable() {
		nbgene = nbremain = nodeOrder.size();
		t_newreg = new int[nbgene][2];
		t_reg = new boolean[nbgene][nbgene];
		bestValue = nbgene+1;
		for (int i=0 ; i<nbgene ; i++) {
			Iterator<RegulatoryMultiEdge> it_reg = regGraph.getIncomingEdges( (RegulatoryVertex) nodeOrder.get(i)).iterator();
			int cpt = 0;
			boolean[] t_regline = t_reg[i];
			while (it_reg.hasNext()) {
				int j = nodeOrder.indexOf(it_reg.next().getSource());
				t_regline[j] = true;
				cpt++;
			}
			if (!t_reg[i][i]) {
				t_reg[i][i] = true;
				cpt++;
			}
			t_newreg[i][0] = i;
			t_newreg[i][1] = cpt;
			if (cpt < bestValue) {
				bestValue = cpt;
				bestIndex = i;
			}
		}
	}
	
	private int selectNext() {
		int choice = bestIndex;
		int ret = t_newreg[choice][0];
		bestValue = nbgene+1;
		bestIndex = -1;
		
		boolean[] t_old;
		
		if (choice != -1) {
			// remove the old one
			t_newreg[choice] = t_newreg[--nbremain];
			t_old = t_reg[choice];
			t_reg[choice] = t_reg[nbremain];
			for (int i=0 ; i<t_old.length ; i++) {
				if (t_old[i]) {
					// here is a new regulator to remove
					for (int j=0 ; j<nbremain ; j++) {
						if (t_reg[j][i]) {
							t_reg[j][i] = false;
							t_newreg[j][1]--;
						}
					}
				}
			}

			// update everything here
			for (int i=0 ; i<nbremain ; i++) {
				if (t_newreg[i][1] < bestValue) {
					bestValue = t_newreg[i][1];
					bestIndex = i;
				}
			}
		}
		return ret;
	}
	
	// "stupid" method
	private OMDDNode buildStableConditionFromParam(int order, int nbChild, OMDDNode param) {
		if (param.next == null || param.level > order) {
			OMDDNode stable = new OMDDNode();
			stable.level = order;
			stable.next = new OMDDNode[nbChild];
			for (int i=0 ; i<stable.next.length ; i++) {
				stable.next[i] = isStable(i, param);
			}
			return stable;
		}
		if (param.level == order) {
			OMDDNode stable = new OMDDNode();
			stable.level = order;
			stable.next = new OMDDNode[nbChild];
			for (int i=0 ; i<stable.next.length ; i++) {
				stable.next[i] = isStable(i, param.next[i]);
			}
			return stable;
		}
		OMDDNode stable = new OMDDNode();
		stable.level = param.level;
		stable.next = new OMDDNode[param.next.length];
		for (int i=0 ; i<stable.next.length ; i++) {
			stable.next[i] = buildStableConditionFromParam(order, nbChild, param.next[i]);
		}
		return stable;
	}
	private OMDDNode isStable(int value, OMDDNode param) {
		if (param.next == null) {
			if (param.value == value) {
				return OMDDNode.TERMINALS[1];
			}
			return OMDDNode.TERMINALS[0];
		}
		OMDDNode stable = new OMDDNode();
		stable.level = param.level;
		stable.next = new OMDDNode[param.next.length];
		for (int i=0 ; i<stable.next.length ; i++) {
			stable.next[i] = isStable(value, param.next[i]);
		}
		return stable;
	}


	// smarter method
	private OMDDNode buildStableConditionFromParam(int order, int nbChild, OMDDNode param, OMDDNode known) {
		if (known.next == null) {
			if (known.value == 0) {
				return OMDDNode.TERMINALS[0];
			}
			return buildStableConditionFromParam(order, nbChild, param);
		}

		OMDDNode stable = new OMDDNode();
		if ((param.next == null || param.level > order) && known.level > order) {
			stable.level = order;
			stable.next = new OMDDNode[nbChild];
			for (int i=0 ; i<stable.next.length ; i++) {
				stable.next[i] = isStable(i, param, known);
			}
		} else if (param.next == null || param.level > known.level) {
			stable.level = known.level;
			stable.next = new OMDDNode[known.next.length];
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
			stable.next = new OMDDNode[param.next.length];
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
			stable.next = new OMDDNode[param.next.length];
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
	
	private OMDDNode isStable(int value, OMDDNode param, OMDDNode known) {
		if (known.next == null) {
			if (known.value == 0) {
				return OMDDNode.TERMINALS[0];
			}
			return isStable(value, param);
		}
		if (param.next == null) {
			if (param.value == value) {
				return known;
			}
			return OMDDNode.TERMINALS[0];
		}
		
		if (param.level < known.level) {
			OMDDNode stable = new OMDDNode();
			stable.level = param.level;
			stable.next = new OMDDNode[param.next.length];
			for (int i=0 ; i<stable.next.length ; i++) {
				stable.next[i] = isStable(value, param.next[i], known);
			}
			return stable;
		}
		if (param.level > known.level) {
			OMDDNode stable = new OMDDNode();
			stable.level = known.level;
			stable.next = new OMDDNode[known.next.length];
			for (int i=0 ; i<stable.next.length ; i++) {
				stable.next[i] = isStable(value, param, known.next[i]);
			}
			return stable;
		}
		// param.level == known.level
		OMDDNode stable = new OMDDNode();
		stable.level = param.level;
		stable.next = new OMDDNode[param.next.length];
		for (int i=0 ; i<stable.next.length ; i++) {
			stable.next[i] = isStable(value, param.next[i], known.next[i]);
		}
		return stable;
	}
	
	
	// show stable state
	private void showStableState (OMDDNode stable) {
		int[] state = new int[nodeOrder.size()];
		for (int i=0 ; i<state.length ; i++) {
			state[i] = -1;
		}
		findStableState(state, stable);
	}
	
	private void findStableState(int[] state, OMDDNode stable) {
		if (stable.next == null) {
			if (stable.value == 1) {
				// we have a stable state:
				System.out.print("stable: ");
				for (int i=0 ; i<state.length ; i++) {
					System.out.print((state[i] != -1 ? ""+state[i] : "*") +" ");
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
