package org.ginsim.service.tool.reg2dyn.updater;

import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.tool.simulation.updater.PriorityClasses;
import org.ginsim.service.tool.reg2dyn.priorityclass.PrioritySetDefinition;

public class UpdaterDefinitionPriorities implements UpdaterDefinition {

	public PrioritySetDefinition pcdef;
	
	@Override
	public SimulationUpdater getUpdater(LogicalModel model) {
		if (USE_BIOLQM_UPDATERS) {
			PriorityClasses pcs = null;
			// TODO: refactor the editing structure of priority classes to use bioLQM's updater 
//			MultipleSuccessorsUpdater lqmUpdater = new PriorityUpdater(model, pcs);
//			return new GenericSimulationUpdater(lqmUpdater);
		}
		return BaseSimulationUpdater.getInstance(model, pcdef);
	}

	@Override
	public String getDefaultName() {
		return "Priorities";
	}

}
