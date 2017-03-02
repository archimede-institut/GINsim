package org.ginsim.service.export.nusmv;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.colomoto.biolqm.LogicalModel;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedState;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStateStore;
import org.ginsim.service.tool.reg2dyn.priorityclass.PrioritySetDefinition;
import org.ginsim.service.tool.reg2dyn.priorityclass.PriorityDefinitionStore;

public class NuSMVConfig implements NamedStateStore, PriorityDefinitionStore {

	public static final int CFG_SYNC = 0;
	public static final int CFG_ASYNC = 1;
	public static final int CFG_PCLASS = 2;

	private RegulatoryGraph graph;
	private LogicalModel model;
	private Map<NamedState, Object> m_initStates;
	private Map<NamedState, Object> m_input;

	private boolean exportStableStates;
	private PrioritySetDefinition priorities;
	private int updatePolicy;
	private Set<String> setFixedInputs;

	/**
	 * @param graph
	 */
	public NuSMVConfig(RegulatoryGraph graph) {
		this.m_initStates = new HashMap<NamedState, Object>();
		this.m_input = new HashMap<NamedState, Object>();
		this.graph = graph;
		this.model = graph.getModel();
		this.updatePolicy = CFG_ASYNC; // Default update policy
		this.setFixedInputs = new HashSet<String>();
		this.exportStableStates = false; 
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
	
	public void setExportStableStates(boolean export) {
		this.exportStableStates = export;
	}
	
	public boolean exportStableStates() {
		return this.exportStableStates;
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

	public void addFixedInput(String nodeID) {
		this.setFixedInputs.add(nodeID);
	}

	public boolean hasFixedInput(String nodeID) {
		return this.setFixedInputs.contains(nodeID);
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
