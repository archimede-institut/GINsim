package org.ginsim.service.export.cadp;

import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;

public class CADPSvlWriter {
	private CADPExportConfig config = null;
	private CADPModuleWriter module = null;
	private CADPIntegrationWriter integration = null;

	public CADPSvlWriter(CADPExportConfig config, CADPModuleWriter module, CADPIntegrationWriter integration) {
		this.config = config;
		this.module = module;
		this.integration = integration;
	}

	public String toString() {
		String out = "";

		// generate all models
		for (int i = 1; i <= config.getTopology().getNumberInstances(); i++) {
			out += "\""
					+ config.getBCGModelFilename(i)
					+ "\""
					+ " = safety reduction of tau*.a reduction of branching reduction of generation of hide all but ";
			int index = 0;
			for (RegulatoryNode visible : config.getListVisible()) {
				if (index++ > 0)
					out += ", ";
				out += visible.getNodeInfo().getNodeID().toUpperCase() + "_"
						+ i;
			}

			if (index > 0)
				out += ", ";

			
			// TODO: change to get the objects from the already generated classes
			// TODO: all these classes should have inits invoked at creation
			// TODO: they should all inherit from a class that known how to write gates and stuff
			
			CADPModuleWriter.InitialStateWriter initialStateWriter = new CADPModuleWriter.InitialStateWriter(config.getInitialStates(), config.getGraph().getNodeOrder());
			CADPModuleWriter.GateWriter gateWriter = new CADPModuleWriter.GateWriter(config.getGraph().getNodeOrder());
			out += "STABLE in ";
			out += "\"" + config.getLNTModelFilename() + "\":"
					+ CADPModuleWriter.concreteProcessName(initialStateWriter.typedStateConcat(i)) + "[" + gateWriter.simpleDecoratedList("_" + i) + "]" + ";\n";
		}
		
		// generate all integration processes
		for (int i = 1; i <= config.getTopology().getNumberInstances(); i++){
			for (RegulatoryNode input : config.getMapping().getMappedInputs()){
				out += "\"" + config.getBCGIntegrationFilename(input, i) + "\"" + " = safety reduction of tau*.a reduction of branching reduction of generation of ";
				// TODO: find the name of the integration process and give appropriate names to gates
				out += "\"" + config.getLNTIntegrationFilename() + "\":" + " AI AI ;\n";
			
			}
		}

		out += "\"" + "composition_" + config.getModelName() + "_"
				+ config.getTopology().getNumberInstances() + ".bcg" + "\"";
		out += " = safety reduction of tau*.a reduction of smart branching reduction of \""
				+ config.getExpFilename() + "\";\n";

		return out;
	}

}
