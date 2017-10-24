package org.ginsim.service.tool.reg2dyn.updater;

import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.tool.simulation.MultipleSuccessorsUpdater;
import org.colomoto.biolqm.tool.simulation.updater.AsynchronousUpdater;

public class UpdaterDefinitionAsynchronous implements UpdaterDefinition {

	public static final UpdaterDefinition DEFINITION = new UpdaterDefinitionAsynchronous();
	
	private UpdaterDefinitionAsynchronous() {
		// private constructor: a single instance is needed
	}
	
	@Override
	public SimulationUpdater getUpdater(LogicalModel model) {
		if (USE_BIOLQM_UPDATERS) {
			MultipleSuccessorsUpdater lqmUpdater = new AsynchronousUpdater(model);
			return new GenericSimulationUpdater(lqmUpdater);
		}
		return new SynchronousSimulationUpdater(model);
	}

	@Override
	public String getDefaultName() {
		return "Asynchronous";
	}

}
