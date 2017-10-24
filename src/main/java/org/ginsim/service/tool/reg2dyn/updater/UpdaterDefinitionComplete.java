package org.ginsim.service.tool.reg2dyn.updater;

import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.tool.simulation.MultipleSuccessorsUpdater;
import org.colomoto.biolqm.tool.simulation.updater.CompleteUpdater;

public class UpdaterDefinitionComplete implements UpdaterDefinition {

	public static final UpdaterDefinition DEFINITION = new UpdaterDefinitionComplete();
	
	private UpdaterDefinitionComplete() {
		// private constructor: a single instance is needed
	}
	
	@Override
	public SimulationUpdater getUpdater(LogicalModel model) {
		MultipleSuccessorsUpdater lqmUpdater = new CompleteUpdater(model);
		return new GenericSimulationUpdater(lqmUpdater);
	}

	@Override
	public String getDefaultName() {
		return "Complete";
	}
}
