package org.ginsim.service.tool.reg2dyn.updater;

import java.util.List;

import org.colomoto.biolqm.LogicalModel;
import org.colomoto.biolqm.NodeInfo;
import org.colomoto.biolqm.tool.simulation.MultipleSuccessorsUpdater;
import org.colomoto.biolqm.tool.simulation.updater.CompleteUpdater;
import org.ginsim.common.xml.XMLWriter;

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
	public String getName() {
		return "Complete";
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
