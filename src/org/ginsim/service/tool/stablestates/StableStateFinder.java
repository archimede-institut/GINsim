package org.ginsim.service.tool.stablestates;

import java.util.List;

import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.MDDVariable;
import org.ginsim.core.graph.common.NodeInfo;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialState;
import org.ginsim.core.graph.regulatorygraph.mutant.Perturbation;
import org.ginsim.core.logicalmodel.LogicalModel;


/**
 * Main entry point to lookup the stable states of a logical model.
 * It relies on the MDD representation of the logical functions and StableOperation
 * to compute stability condition for all components of the model.
 * 
 * @author Aurelien Naldi
 */
public class StableStateFinder implements StableStateSearcherNew {
	private final MDDManager m_factory;
	private final LogicalModel model;
	
	Perturbation p;
	Iterable<Integer> ordering;

	InitialState stateRestriction = null; // no state restriction by default
	boolean[][] t_reg;
	int[][] t_newreg;
	
	int bestIndex, bestValue;
	int nbgene, nbremain;
	
	public StableStateFinder(RegulatoryGraph lrg) {
		this.model = lrg.getModel();
		m_factory = model.getMDDFactory();
	}

	@Override
	public MDDManager getFactory() {
		return m_factory;
	}
	
	@Override
	public void setPerturbation(Perturbation p) {
		this.p = p;
	}
	
	@Override
	public Integer call() {
		LogicalModel workingModel = model;
		ordering = new StructuralNodeOrderer(model);
		StableOperation sop = new StableOperation();
		if (p != null) {
			workingModel = model.clone();
			p.apply(workingModel);
		}
		int[] mdds = workingModel.getLogicalFunctions();

		// search domain: all nodes or simple restriction
		int prev=1;
		if (stateRestriction != null) {
			prev = stateRestriction.getMDD(m_factory);
		}
		
		// loop over the existing nodes!
		int result=prev;
		List<NodeInfo> nodes = workingModel.getNodeOrder();
		for (int i: ordering) {
			NodeInfo node = nodes.get(i);
			prev = result;
			MDDVariable var = m_factory.getVariableForKey(node);
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
		this.stateRestriction = state;
	}
}
