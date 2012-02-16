package org.ginsim.service.tool.stablestates;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.python.antlr.PythonParser.factor_return;

import fr.univmrs.tagc.javaMDD.MDDFactory;

/**
 * Main entry point to lookup the stable states of a logical model.
 * It relies on the MDD representation of the logical functions and StableOperation
 * to compute stability condition for all components of the model.
 * 
 * @author Aurelien Naldi
 */
public class StableStateFinder {
	RegulatoryGraph model;
	MDDFactory m_factory;
	
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
	
	public int find() {
		StableOperation sop = new StableOperation();
		
		// loop over the existing nodes!
		int prev=1, result=1;
		for (RegulatoryNode node: model.getNodeOrder()) {
			prev = result;
			int var = m_factory.getVariableID(node);
			int f = node.getMDD(model, m_factory);
			result = sop.getStable(m_factory, prev, f, var);
			m_factory.free(prev);
			m_factory.free(f);
		}
		return result;
	}
}
