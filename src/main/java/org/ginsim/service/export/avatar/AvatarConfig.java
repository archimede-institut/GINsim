package org.ginsim.service.export.avatar;

import java.util.HashMap;
import java.util.Map;

import org.colomoto.logicalmodel.LogicalModel;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedState;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStateStore;

public class AvatarConfig implements NamedStateStore {

	private RegulatoryGraph graph;
	private LogicalModel model;
	private Map<NamedState, Object> m_initStates;
	private Map<NamedState, Object> m_input;

	/**
	 * @param graph
	 */
	public AvatarConfig(RegulatoryGraph graph) {
		m_initStates = new HashMap<NamedState, Object>();
		m_input = new HashMap<NamedState, Object>();
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
