package org.ginsim.service.export.sat;

import java.util.HashMap;
import java.util.Map;

import org.colomoto.logicalmodel.LogicalModel;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedState;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStateStore;

public class SATConfig implements NamedStateStore {

	private RegulatoryGraph graph;
	private LogicalModel model;
	private Map<NamedState, Object> m_initStates;
	private Map<NamedState, Object> m_input;
	private SATExportType type;

	/**
	 * @param graph
	 */
	public SATConfig(RegulatoryGraph graph) {
		this.m_initStates = new HashMap<NamedState, Object>();
		this.m_input = new HashMap<NamedState, Object>();
		this.graph = graph;
		this.model = graph.getModel();
		this.type = SATExportType.STABILITY_CONDITION;
	}

	public void updateModel(LogicalModel model) {
		this.model = model;
	}

	public Map<NamedState, Object> getInitialState() {
		return this.m_initStates;
	}

	public Map<NamedState, Object> getInputState() {
		return this.m_input;
	}

	public RegulatoryGraph getGraph() {
		return this.graph;
	}

	public LogicalModel getModel() {
		return this.model;
	}

	public void setExportType(SATExportType type) {
		this.type = type;
	}

	public SATExportType getExportType() {
		return this.type;
	}
}
