package org.ginsim.service.tool.stablestates;

import java.util.List;

import org.colomoto.biolqm.LQMServiceManager;
import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.tool.fixpoints.FixpointSearcher;
import org.colomoto.biolqm.tool.fixpoints.FixpointService;
import org.colomoto.biolqm.tool.fixpoints.FixpointTask;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.EStatus;
import org.ginsim.core.service.Service;
import org.ginsim.core.service.Alias;
import org.ginsim.core.service.ServiceStatus;
import org.kohsuke.MetaInfServices;


/**
 * This service provides an analytic search for stable states, implemented in bioLQM.
 * A state "x" is stable if, for every gene "i", K(x) = x(i).
 *
 * @author Aurelien Naldi
 */
@MetaInfServices( Service.class)
@Alias("stable")
@ServiceStatus(EStatus.RELEASED)
public class StableStatesService implements Service {

	public final static FixpointService BACKEND = LQMServiceManager.get(FixpointService.class);

	/**
	 * This constructor should be called by the service manager,
	 * other users will have to get the first instance
	 */
	public StableStatesService() {
	}

	public static FixpointTask getTask(LogicalModel model) {
		FixpointTask task = BACKEND.getTask(model);
		task.pattern = true;
		return task;
	}

	public FixpointTask getTask(RegulatoryGraph graph) {
		return getTask(graph.getModel());
	}

	/**
	 * @param model
	 * @return a MDD-based stable-state searcher
	 * @deprecated This low-level API could change or be removed
	 */
	public FixpointSearcher getSearcher( LogicalModel model) {
		return new FixpointSearcher( model);
	}

	/**
	 * @param graph
	 * @return a MDD-based stable-state searcher
	 * @deprecated This low-level API could change or be removed
	 */
	public FixpointSearcher getSearcher( RegulatoryGraph graph) {
		return getSearcher( graph.getModel());
	}

}
