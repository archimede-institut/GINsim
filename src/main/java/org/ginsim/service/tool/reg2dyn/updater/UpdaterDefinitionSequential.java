package org.ginsim.service.tool.reg2dyn.updater;

import java.util.List;

import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.NodeInfo;
import org.colomoto.biolqm.tool.simulation.DeterministicUpdater;
import org.colomoto.biolqm.tool.simulation.updater.SequentialUpdater;

public class UpdaterDefinitionSequential implements UpdaterDefinition {

	private String name = "Sequential";
	
	@Override
	public SimulationUpdater getUpdater(LogicalModel model) {
		DeterministicUpdater lqmUpdater = new SequentialUpdater(model);
		return new DeterministicSimulationUpdater(lqmUpdater);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public String summary(List<NodeInfo> nodeOrder) {
		// TODO: custom sequential updating will need a summary
		return getName();
	}
}
