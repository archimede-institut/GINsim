package org.ginsim.service.tool.stablestates;

import java.util.List;

import org.colomoto.logicalmodel.LogicalModel;
import org.colomoto.logicalmodel.tool.stablestate.StableStateSearcher;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.mutant.Perturbation;
import org.ginsim.core.service.Service;
import org.ginsim.core.service.Alias;
import org.mangosdk.spi.ProviderFor;


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
@Alias("stable")
public class StableStatesService implements Service {

	/**
	 * This constructor should be called by the service manager,
	 * other users will have to get the first instance
	 */
	public StableStatesService() {
	}

	public StableStateSearcher getSearcher(RegulatoryGraph graph) {
		return getStableStateSearcher(graph.getModel());
	}
	
	public StableStateSearcher getStableStateSearcher( RegulatoryGraph regGraph, List<RegulatoryNode> nodeOrder, Perturbation mutant) {
		LogicalModel model = regGraph.getModel();
		if (mutant != null) {
			mutant.update(model);
		}
		return getStableStateSearcher(model);
	}

	public StableStateSearcher getStableStateSearcher( LogicalModel model) {
		return new StableStateSearcher(model);
	}

}
