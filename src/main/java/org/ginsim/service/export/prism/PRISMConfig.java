package org.ginsim.service.export.prism;

import java.util.HashMap;
import java.util.Map;

import org.colomoto.logicalmodel.LogicalModel;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedState;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStateStore;

public class PRISMConfig implements NamedStateStore {

	public static final int CFG_SYNC = 0;
	public static final int CFG_ASYNC = 1;

	private RegulatoryGraph graph;
	private LogicalModel model;
	private Map<NamedState, Object> m_initStates;
	private Map<NamedState, Object> m_input;

	/**
	 * @param graph
	 */
	public PRISMConfig(RegulatoryGraph graph) {
		this.m_initStates = new HashMap<NamedState, Object>();
		this.m_input = new HashMap<NamedState, Object>();
		this.graph = graph;
		this.model = graph.getModel();
	}

	public void updateModel(LogicalModel model) {
		this.model = model;
	}

	public Map<NamedState, Object> getInitialState() {
		return m_initStates;
	}

	public Map<NamedState, Object> getInputState() {
		return m_input;
	}

	public RegulatoryGraph getGraph() {
		return graph;
	}

	public LogicalModel getModel() {
		return model;
	}
}
