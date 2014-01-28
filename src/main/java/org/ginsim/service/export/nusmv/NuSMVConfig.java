package org.ginsim.service.export.nusmv;

import java.util.HashMap;
import java.util.Map;

import org.colomoto.logicalmodel.LogicalModel;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialState;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialStateStore;
import org.ginsim.service.tool.reg2dyn.priorityclass.PrioritySetDefinition;
import org.ginsim.service.tool.reg2dyn.priorityclass.PriorityDefinitionStore;

public class NuSMVConfig implements InitialStateStore, PriorityDefinitionStore {

	public static final int CFG_SYNC = 0;
	public static final int CFG_ASYNC = 1;
	public static final int CFG_PCLASS = 2;

	private RegulatoryGraph graph;
	private LogicalModel model;
	private Map<InitialState, Object> m_initStates;
	private Map<InitialState, Object> m_input;

    private PrioritySetDefinition priorities;
	private int updatePolicy;

	/**
	 * @param graph
	 */
	public NuSMVConfig(RegulatoryGraph graph) {
		m_initStates = new HashMap<InitialState, Object>();
		m_input = new HashMap<InitialState, Object>();
		this.graph = graph;
		this.model = graph.getModel();
		updatePolicy = CFG_ASYNC; // Default update policy
	}

	public void setUpdatePolicy() {
		if (priorities == null)
			updatePolicy = CFG_ASYNC;
		else if (priorities.size() == 1) {
			if (priorities.getPclass(graph.getNodeInfos())[0][1] == 0)
				updatePolicy = CFG_SYNC;
			else
				updatePolicy = CFG_ASYNC;
		} else
			updatePolicy = CFG_PCLASS;
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
