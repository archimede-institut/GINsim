package org.ginsim.service.tool.reg2dyn.updater;

import org.colomoto.biolqm.LogicalModel;

public interface UpdaterDefinition {

	public final static boolean USE_BIOLQM_UPDATERS = true;

	SimulationUpdater getUpdater(LogicalModel model);
	
	String getDefaultName();
}
