package org.ginsim.service.tool.stablestates;

import java.util.List;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialState;
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
	private final RegulatoryGraph model;
	private final MDDFactory m_factory;
	
	Perturbation p;
	Iterable<Integer> ordering;

	InitialState state = null; // no state restriction by default
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

	@Override
	public MDDFactory getFactory() {
		return m_factory;
	}
	
	@Override
	public void setPerturbation(Perturbation p) {
		this.p = p;
	}
	
	@Override
	public Integer call() {
		ordering = new StructuralNodeOrderer(model);
		StableOperation sop = new StableOperation();
		int[] mdds = model.getMDDs(m_factory);
		if (p != null) {
			mdds = p.apply(m_factory, mdds, model);
		}

		// loop over the existing nodes!
		int prev=1;
		if (state != null) {
			prev = state.getMDD(m_factory);
		}
		int result=prev;
		List<RegulatoryNode> nodes = model.getNodeOrder();
		for (int i: ordering) {
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

	@Override
	public void setStateRestriction( InitialState state) {
		this.state = state;
	}
}
