package org.ginsim.service.tool.stablestates;

import java.util.Iterator;
import java.util.List;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;

public class StructuralNodeOrderer implements Iterable<Integer> {

	protected final RegulatoryGraph regGraph;

	public StructuralNodeOrderer( RegulatoryGraph regGraph) {
		this.regGraph = regGraph;
	}

	@Override
	public Iterator<Integer> iterator() {
		return new NodeIterator(regGraph);
	}

}

class NodeIterator implements Iterator<Integer> {
	
	private final RegulatoryGraph regGraph;
	private final List<RegulatoryNode> nodeOrder;

	int nbgene, nbremain;
	int bestIndex, bestValue;
	
	boolean[][] t_reg;
	int[][] t_newreg;

	
	public NodeIterator(RegulatoryGraph regGraph) {
		this.regGraph = regGraph;
		this.nodeOrder = regGraph.getNodeOrder();
		buildAdjTable();
	}

	private void buildAdjTable() {
		nbgene = nbremain = nodeOrder.size();
		t_newreg = new int[nbgene][2];
		t_reg = new boolean[nbgene][nbgene];
		bestValue = nbgene+1;
		for (int i=0 ; i<nbgene ; i++) {
			Iterator<RegulatoryMultiEdge> it_reg = regGraph.getIncomingEdges( (RegulatoryNode) nodeOrder.get(i)).iterator();
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

}
