package org.ginsim.service.export.prism;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.colomoto.logicalmodel.LogicalModel;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedState;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStateStore;
import org.ginsim.service.tool.reg2dyn.priorityclass.PrioritySetDefinition;
import org.ginsim.service.tool.reg2dyn.priorityclass.PriorityDefinitionStore;

public class PRISMConfig implements NamedStateStore, PriorityDefinitionStore {

	public static final int CFG_SYNC = 0;
	public static final int CFG_ASYNC = 1;

	private RegulatoryGraph graph;
	private LogicalModel model;
	private Map<NamedState, Object> m_initStates;
	private Map<NamedState, Object> m_input;

	private PrioritySetDefinition priorities;
	private int updatePolicy;

	/**
	 * @param graph
	 */
	public PRISMConfig(RegulatoryGraph graph) {
		this.m_initStates = new HashMap<NamedState, Object>();
		this.m_input = new HashMap<NamedState, Object>();
		this.graph = graph;
		this.model = graph.getModel();
		this.updatePolicy = CFG_ASYNC; // Default update policy
	}

	public void setUpdatePolicy() {
		if (priorities == null)
			updatePolicy = CFG_ASYNC;
		else if (priorities.size() == 1) {
			if (priorities.getPclass(graph.getNodeInfos())[0][1] == 0)
				updatePolicy = CFG_SYNC;
			else
				updatePolicy = CFG_ASYNC;
		}
	}

	public void updateModel(LogicalModel model) {
		this.model = model;
	}

	public void setUpdatePolicy(int policy) {
		updatePolicy = policy;
	}

	public int getUpdatePolicy() {
		return updatePolicy;
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

	@Override
	public PrioritySetDefinition getPriorityDefinition() {
		return priorities;
	}

	@Override
	public void setPriorityDefinition(PrioritySetDefinition pcdef) {
		this.priorities = pcdef;
		setUpdatePolicy();
	}
}