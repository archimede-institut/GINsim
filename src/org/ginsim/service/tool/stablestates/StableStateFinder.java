package org.ginsim.service.tool.stablestates;

import java.util.List;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.NodeInfo;
import org.colomoto.logicalmodel.tool.stablestate.StableOperation;
import org.colomoto.logicalmodel.tool.stablestate.StableStateSearcher;
import org.colomoto.logicalmodel.tool.stablestate.StructuralNodeOrderer;
import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.MDDVariable;
import org.colomoto.mddlib.PathSearcher;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialState;
import org.ginsim.core.graph.regulatorygraph.mutant.Perturbation;


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
		this(lrg.getModel());
	}
	
	public StableStateFinder(LogicalModel model) {
		this.model = model;
		m_factory = model.getMDDManager();
		
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
		if (p != null) {
			workingModel = model.clone();
			p.apply(workingModel);
		}

		if (stateRestriction != null) {
			throw new RuntimeException("state restriction unsupported");
		}
		
		StableStateSearcher searcher = new StableStateSearcher(workingModel);
		return searcher.getResult();
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
