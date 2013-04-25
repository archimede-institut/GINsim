package org.ginsim.service.format.ginml;

import org.colomoto.logicalmodel.LogicalModel;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.perturbation.PerturbationStore;

public class GINMLFormatConfig {

	private LogicalModel model;
	private RegulatoryGraph graph;
	private PerturbationStore mutantStore;

	public GINMLFormatConfig(RegulatoryGraph graph) {
		this.graph = graph;
		this.model = graph.getModel();
		mutantStore = new PerturbationStore();
	}

	public void updateModel(LogicalModel model) {
		this.model = model;
	}
	
	public PerturbationStore getStore() {
		return mutantStore;
	}
	
	public LogicalModel getModel() {
		return model;
	}

	public RegulatoryGraph getGraph() {
		return graph;
	}
}
