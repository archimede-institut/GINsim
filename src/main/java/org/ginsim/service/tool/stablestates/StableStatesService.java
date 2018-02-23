package org.ginsim.service.tool.stablestates;

import java.util.List;

import org.colomoto.biolqm.LQMServiceManager;
import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.tool.fixpoints.FixpointSearcher;
import org.colomoto.biolqm.tool.fixpoints.FixpointSettings;
import org.colomoto.biolqm.tool.fixpoints.FixpointTask;
import org.colomoto.biolqm.tool.fixpoints.FixpointTool;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.perturbation.Perturbation;
import org.ginsim.core.service.EStatus;
import org.ginsim.core.service.Service;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.ServiceStatus;
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
@ServiceStatus(EStatus.RELEASED)
public class StableStatesService implements Service {

	public static FixpointTool tool = LQMServiceManager.getTool(FixpointTool.class);

	/**
	 * This constructor should be called by the service manager,
	 * other users will have to get the first instance
	 */
	public StableStatesService() {
	}

	public static FixpointTask getTask(LogicalModel model) {
		FixpointSettings settings = tool.getSettings(model);
		settings.pattern = true;
		return tool.getTask(settings);
	}

	public FixpointSearcher getSearcher(RegulatoryGraph graph) {
		return getStableStateSearcher(graph.getModel());
	}
	
	public FixpointSearcher getStableStateSearcher( RegulatoryGraph regGraph, List<RegulatoryNode> nodeOrder, Perturbation mutant) {
		LogicalModel model = regGraph.getModel();
		if (mutant != null) {
			mutant.update(model);
		}
		return getStableStateSearcher(model);
	}

	public FixpointSearcher getStableStateSearcher( LogicalModel model) {
		return new FixpointSearcher(model);
	}

}
