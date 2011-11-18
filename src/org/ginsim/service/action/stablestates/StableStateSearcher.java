package org.ginsim.service.action.stablestates;

import java.util.List;

import org.ginsim.graph.regulatorygraph.GsRegulatoryVertex;

import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.Perturbation;

public interface StableStateSearcher {
	
	void setPerturbation( Perturbation perturbation);
	
	OmddNode getStables();

	void setNodeOrder(List<GsRegulatoryVertex> sortedVars, OmddNode[] tReordered);
}
