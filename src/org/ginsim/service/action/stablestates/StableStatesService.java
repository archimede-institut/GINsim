package org.ginsim.service.action.stablestates;

import java.util.List;

import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.service.Service;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.tagc.GINsim.regulatoryGraph.mutant.Perturbation;

/**
 * This implements an analytic search of stable states. A state "x" is stable if, for every gene "i",
 * K(x) = x(i).
 * 
 * To find a stable state, one can build a MDD for each gene, giving the context under which 
 * THIS gene is stable.Then the stable states can be found by combining these diagrams.
 * 
 * To improve performances, the individuals "stability" MDD are not built independently 
 * but immediately assembled.
 * The order in which they are considerd is also chosen to keep them small as long as possible.
 */
@ProviderFor( Service.class)
public class StableStatesService implements Service {

	/**
	 * This constructor should be called by the service manager,
	 * other users will have to get the first instance
	 */
	public StableStatesService() {
	}

	public StableStateSearcher getSearcher(RegulatoryGraph graph) {
		return new StableStatesAlgoImpl( graph);
	}
	
	public OmddNode run( RegulatoryGraph regGraph, List nodeOrder, Perturbation mutant) {
		StableStateSearcher searcher = getSearcher(regGraph);
		searcher.setPerturbation(mutant);
		return searcher.getStables();
	}

	public OmddNode run( RegulatoryGraph regGraph, List nodeOrder, Perturbation mutant, OmddNode[] trees) {
		StableStateSearcher algo = getSearcher( regGraph);
		algo.setPerturbation(mutant);
		return algo.getStables();
	}
}
