package fr.univmrs.ibdm.GINsim.stableStates;

import java.util.Iterator;
import java.util.List;

import fr.univmrs.ibdm.GINsim.data.GsDirectedEdge;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.graph.GsGraphManager;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.mutant.GsRegulatoryMutantDef;

/**
 * This implements an analytic search of stable states. A state "x" is stable if, for every gene "i",
 * K(x) = x(i).
 * 
 * To find a stable state, one can build a MDD for each gene, giving the context under which 
 * THIS gene is stable.Then the stable states can be found by combining these diagrams.
 * 
 * To improve performances, the individuals "stability" MDD are not built independantly 
 * but immediately assembled.
 * The order in which they are considerd is also chosen to keep them small as long as possible.
 */
public class GsSearchStableStates extends Thread {

	private GsRegulatoryGraph regGraph;
	private GenericStableStateUI ui;
	List nodeOrder;
	OmddNode[] t_param;
	OmddNode dd_stable;
	GsRegulatoryMutantDef mutant;
	boolean[][] t_reg;
	int[][] t_newreg;
	
	/** use a reordering to improve the size of the MDD ? */
	static final boolean ORDERTEST = true;
	
	int bestIndex, bestValue;
	int nbgene, nbremain;

	public GsSearchStableStates(GsGraph regGraph, GsRegulatoryMutantDef mutant, GenericStableStateUI ui) {
		this.regGraph = (GsRegulatoryGraph)regGraph;
		this.nodeOrder = regGraph.getNodeOrder();
		this.mutant = mutant;
		this.ui = ui;
	}

	public OmddNode getStable() {
		if (ORDERTEST) {
			buildAdjTable();
		}
		
		t_param = regGraph.getAllTrees(true);
		if (mutant != null) {
			mutant.apply(t_param, regGraph);
		}
		
		dd_stable = OmddNode.TERMINALS[1];
		for (int i=0 ; i<t_param.length ; i++) {
			if (ORDERTEST) {
				int sel = selectNext();
				dd_stable = buildStableConditionFromParam(sel, 
						((GsRegulatoryVertex)nodeOrder.get(sel)).getMaxValue()+1,
						t_param[sel],
						dd_stable).reduce();
			} else {
				if (i%10 == 0) {
					System.out.println("  "+i);
				}
				dd_stable = buildStableConditionFromParam(i, 
						((GsRegulatoryVertex)nodeOrder.get(i)).getMaxValue()+1,
						t_param[i],
						dd_stable).reduce();
			}
		}
		return dd_stable;
	}
	
	public void run() {
		showStableState(getStable());
	}
	
	private void buildAdjTable() {
		nbgene = nbremain = nodeOrder.size();
		GsGraphManager manager = regGraph.getGraphManager();
		t_newreg = new int[nbgene][2];
		t_reg = new boolean[nbgene][nbgene];
		bestValue = nbgene+1;
		for (int i=0 ; i<nbgene ; i++) {
			Iterator it_reg = manager.getIncomingEdges(nodeOrder.get(i)).iterator();
			int cpt = 0;
			boolean[] t_regline = t_reg[i];
			while (it_reg.hasNext()) {
				int j = nodeOrder.indexOf(((GsDirectedEdge)it_reg.next()).getSourceVertex());
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
		if (ui != null) {
			ui.setResult(stable);
			return;
		}
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

	public void setMutant(GsRegulatoryMutantDef mutant) {
		this.mutant = mutant;
	}
}
