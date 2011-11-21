package org.ginsim.service.action.stablestates;

import java.util.List;

import org.ginsim.graph.regulatorygraph.RegulatoryVertex;
import org.ginsim.graph.regulatorygraph.mutant.Perturbation;
import org.ginsim.graph.regulatorygraph.omdd.OMDDNode;


public interface StableStateSearcher {
	
	void setPerturbation( Perturbation perturbation);
	
	OMDDNode getStables();

	void setNodeOrder(List<RegulatoryVertex> sortedVars, OMDDNode[] tReordered);
}
