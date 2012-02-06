package org.ginsim.service.tool.stablestates;

import java.util.List;
import java.util.concurrent.Callable;

import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.mutant.Perturbation;
import org.ginsim.core.graph.regulatorygraph.omdd.OMDDNode;


public interface StableStateSearcher extends Callable<OMDDNode> {
	
	void setPerturbation( Perturbation perturbation);
	
	void setNodeOrder(List<RegulatoryNode> sortedVars);
}
