package org.ginsim.service.tool.stablestates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.colomoto.mddlib.MDDManager;
import org.ginsim.core.graph.common.NodeInfo;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.logicalmodel.LogicalModel;


public class StructuralNodeOrderer implements Iterable<Integer> {

	protected final RegulatoryGraph regGraph;
	protected final LogicalModel model;

	public StructuralNodeOrderer( RegulatoryGraph regGraph) {
		this.regGraph = regGraph;
		this.model = null;
	}

	public StructuralNodeOrderer( LogicalModel model) {
		this.regGraph = null;
		this.model = model;
	}

	@Override
	public Iterator<Integer> iterator() {
		if (regGraph != null) {
			return new NodeIterator(regGraph);
		}
		return new NodeIterator(model);
	}

}

class NodeIterator implements Iterator<Integer> {
	
	int nbgene, nbremain;
	int bestIndex, bestValue;
	
	boolean[][] t_reg;
	int[][] t_newreg;

	
	public NodeIterator(RegulatoryGraph regGraph) {
		buildAdjTable(regGraph);
	}

	public NodeIterator(LogicalModel model) {
		buildAdjTable(model);
	}

	private void buildAdjTable(RegulatoryGraph regGraph) {
		List<RegulatoryNode> nodeOrder = regGraph.getNodeOrder();
		nbgene = nbremain = nodeOrder.size();
		t_newreg = new int[nbgene][2];
		t_reg = new boolean[nbgene][nbgene];
		bestValue = nbgene+1;
		for (int i=0 ; i<nbgene ; i++) {
			Iterator<RegulatoryMultiEdge> it_reg = regGraph.getIncomingEdges( nodeOrder.get(i)).iterator();
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

	private void buildAdjTable(LogicalModel model) {
		List<NodeInfo> nodeOrder = model.getNodeOrder();
		MDDManager factory = model.getMDDFactory();
		nbgene = nbremain = nodeOrder.size();
		t_newreg = new int[nbgene][2];
		t_reg = new boolean[nbgene][nbgene];
		bestValue = nbgene+1;
		int[] functions = model.getLogicalFunctions();
		for (int i=0 ; i<nbgene ; i++) {
			Collection<Integer> regulators = getRegulators(factory, functions[i]);
			int cpt = 0;
			boolean[] t_regline = t_reg[i];
			for (int r: regulators) {
				t_regline[r] = true;
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

	@Override
	public Integer next() {
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

	@Override
	public boolean hasNext() {
		return nbremain > 0;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException();
	}

	
	private static Collection<Integer> getRegulators(MDDManager factory, int f) {
		Set<Integer> regulators = new HashSet<Integer>();
		addRegulators(regulators, factory, f);
		return regulators;
	}
	
	private static void addRegulators(Set<Integer> regulators, MDDManager factory, int f) {
		if (factory.isleaf(f)) {
			return;
		}
		
		int n = factory.getLevel(f);
		regulators.add(n);
		n = factory.getNbValues( n );
		for (int v=0 ; v<n ; v++) {
			addRegulators(regulators, factory, factory.getChild(f, v));
		}
	}
}
