package org.ginsim.service.export.avatar;

import java.util.HashMap;
import java.util.Map;

import org.colomoto.logicalmodel.LogicalModel;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialState;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialStateStore;

public class AvatarConfig implements InitialStateStore {

	private RegulatoryGraph graph;
	private LogicalModel model;
	private Map<InitialState, Object> m_initStates;
	private Map<InitialState, Object> m_input;

	/**
	 * @param graph
	 */
	public AvatarConfig(RegulatoryGraph graph) {
		m_initStates = new HashMap<InitialState, Object>();
		m_input = new HashMap<InitialState, Object>();
		this.graph = graph;
		this.model = graph.getModel();
	}

	public void updateModel(LogicalModel model) {
		this.model = model;
	}

	public Map<InitialState, Object> getInitialState() {
		return m_initStates;
	}

	public Map<InitialState, Object> getInputState() {
		return m_input;
	}

	public RegulatoryGraph getGraph() {
		return graph;
	}

	public LogicalModel getModel() {
		return model;
	}
}
