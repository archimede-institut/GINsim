package org.ginsim.service.tool.reg2dyn.updater;

import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.tool.simulation.DeterministicUpdater;
import org.colomoto.biolqm.tool.simulation.updater.SequentialUpdater;

public class UpdaterDefinitionSequential implements UpdaterDefinition {

	@Override
	public SimulationUpdater getUpdater(LogicalModel model) {
		DeterministicUpdater lqmUpdater = new SequentialUpdater(model);
		return new DeterministicSimulationUpdater(lqmUpdater);
	}

	@Override
	public String getDefaultName() {
		return "Sequential";
	}

}
