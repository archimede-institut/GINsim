package org.ginsim.service.export.nusmv;

import java.util.HashMap;
import java.util.Map;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialState;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialStateStore;
import org.ginsim.core.graph.regulatorygraph.mutant.Perturbation;
import org.ginsim.core.utils.data.ObjectStore;
import org.ginsim.service.tool.reg2dyn.priorityclass.PriorityClassDefinition;


public class NuSMVConfig implements InitialStateStore {

	public static final int CFG_SYNC = 0;
	public static final int CFG_ASYNC = 1;
	public static final int CFG_PCLASS = 2;
	public static final int CFG_INPUT_FROZEN = 10;
	public static final int CFG_INPUT_IVAR = 11;

	private RegulatoryGraph graph;
	private Map<InitialState, Object> m_initStates;
	private Map<InitialState, Object> m_input;
	
	// Store has two objects: 0- Mutant & 1- PriorityClass
	public ObjectStore store = new ObjectStore(2);
	public Perturbation mutant;
	private int updatePolicy;
	private int exportType;

	/**
	 * @param graph
	 */
	public NuSMVConfig(RegulatoryGraph graph) {
		m_initStates = new HashMap<InitialState, Object>();
		m_input = new HashMap<InitialState, Object>();
		this.graph = graph;
		updatePolicy = CFG_ASYNC; // Default update policy
		exportType = CFG_INPUT_FROZEN; // Default export type
	}
	
	public void setUpdatePolicy() {
		PriorityClassDefinition priorities = (PriorityClassDefinition) store
				.getObject(1);
		if (priorities == null)
			updatePolicy = CFG_ASYNC;
		else if (priorities.getNbElements() == 1) {
			if (priorities.getPclass(graph.getNodeInfos())[0][1] == 0)
				updatePolicy = CFG_SYNC;
			else
				updatePolicy = CFG_ASYNC;
		} else
			updatePolicy = CFG_PCLASS;
	}
	
	public void setUpdatePolicy(int policy) {
		updatePolicy = policy;
	}

	public int getUpdatePolicy() {
		return updatePolicy;
	}

	public void setExportType(int type) {
		exportType = type;
	}

	public int getExportType() {
		return exportType;
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
}

