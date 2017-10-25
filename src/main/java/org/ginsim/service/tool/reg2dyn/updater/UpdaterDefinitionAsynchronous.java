package org.ginsim.service.tool.reg2dyn.updater;

import java.util.List;

import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.NodeInfo;
import org.colomoto.biolqm.tool.simulation.MultipleSuccessorsUpdater;
import org.colomoto.biolqm.tool.simulation.updater.AsynchronousUpdater;
import org.ginsim.common.xml.XMLWriter;

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
	public String getName() {
		return "Asynchronous";
	}

	@Override
	public void setName(String name) {
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public String summary(List<NodeInfo> nodeOrder) {
		return getName();
	}

	@Override
	public void toXML(XMLWriter out) {
	}
}
