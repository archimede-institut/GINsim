package org.ginsim.service.tool.stablestates;

import java.util.List;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.mutant.Perturbation;

import fr.univmrs.tagc.javaMDD.MDDFactory;

/**
 * Main entry point to lookup the stable states of a logical model.
 * It relies on the MDD representation of the logical functions and StableOperation
 * to compute stability condition for all components of the model.
 * 
 * @author Aurelien Naldi
 */
public class StableStateFinder implements StableStateSearcherNew {
	RegulatoryGraph model;
	MDDFactory m_factory;
	Perturbation p;
	
	boolean[][] t_reg;
	int[][] t_newreg;
	
	int bestIndex, bestValue;
	int nbgene, nbremain;
	
	public StableStateFinder(RegulatoryGraph lrg) {
		this(lrg, lrg.getMDDFactory());
	}

	public StableStateFinder(RegulatoryGraph lrg, MDDFactory factory) {
		model = lrg;
		m_factory = factory;
	}

	
	public MDDFactory getFactory() {
		return m_factory;
	}
	
	public void setPerturbation(Perturbation p) {
		this.p = p;
	}
	
	public Integer call() {
		StableOperation sop = new StableOperation();
		int[] mdds = model.getMDDs(m_factory);
		if (p != null) {
			mdds = p.apply(m_factory, mdds, model);
		}

		// loop over the existing nodes!
		int prev=1, result=1;
		List<RegulatoryNode> nodes = model.getNodeOrder();
		for (int i=0 ; i<mdds.length ; i++) {
			RegulatoryNode node = nodes.get(i);
			prev = result;
			int var = m_factory.getVariableID(node);
			int f = mdds[i];
			result = sop.getStable(m_factory, prev, f, var);
			m_factory.free(prev);
		}
		return result;
	}

	@Override
	public void setNodeOrder(List<RegulatoryNode> sortedVars) {
		// TODO select node order
		throw new RuntimeException("custom node order unsupported");
	}
}
