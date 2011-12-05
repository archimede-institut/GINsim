package org.ginsim.service.tool.stablestates;

import java.util.List;

import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.mutant.Perturbation;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;


public interface StableStateSearcher {
	
	void setPerturbation( Perturbation perturbation);
	
	OMDDNode getStables();

	void setNodeOrder(List<RegulatoryNode> sortedVars, OMDDNode[] tReordered);
}
