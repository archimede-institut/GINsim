package org.ginsim.service.tool.reg2dyn.updater;

import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.tool.simulation.DeterministicUpdater;
import org.colomoto.biolqm.tool.simulation.updater.SynchronousUpdater;

public class UpdaterDefinitionSynchronous implements UpdaterDefinition {

	public static final UpdaterDefinition DEFINITION = new UpdaterDefinitionSynchronous();
	
	private UpdaterDefinitionSynchronous() {
		// private constructor: a single instance is needed
	}

	@Override
	public SimulationUpdater getUpdater(LogicalModel model) {
		if (USE_BIOLQM_UPDATERS) {
			DeterministicUpdater lqmUpdater = new SynchronousUpdater(model);
			return new DeterministicSimulationUpdater(lqmUpdater);
		}
		return new SynchronousSimulationUpdater(model);
	}

	@Override
	public String getDefaultName() {
		return "Synchronous";
	}
}