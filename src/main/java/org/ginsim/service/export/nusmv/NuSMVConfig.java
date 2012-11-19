package org.ginsim.service.export.nusmv;

import java.util.HashMap;
import java.util.Map;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialState;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialStateStore;
import org.ginsim.core.graph.regulatorygraph.perturbation.PerturbationHolder;
import org.ginsim.core.graph.regulatorygraph.perturbation.PerturbationStore;
import org.ginsim.core.graph.regulatorygraph.perturbation.PerturbationUser;
import org.ginsim.core.utils.data.ObjectStore;
import org.ginsim.service.tool.reg2dyn.priorityclass.PriorityClassDefinition;

public class NuSMVConfig implements InitialStateStore {

	public static final int CFG_SYNC = 0;
	public static final int CFG_ASYNC = 1;
	public static final int CFG_PCLASS = 2;

	private RegulatoryGraph graph;
	private Map<InitialState, Object> m_initStates;
	private Map<InitialState, Object> m_input;

	// Store has one object: 0- PriorityClass
	private ObjectStore store = new ObjectStore(1);
	private PerturbationHolder perturbationstore;
	private int updatePolicy;

	/**
	 * @param graph
	 */
	public NuSMVConfig(RegulatoryGraph graph) {
		m_initStates = new HashMap<InitialState, Object>();
		m_input = new HashMap<InitialState, Object>();
		this.graph = graph;
		perturbationstore = new PerturbationStore();
		// FIXME: use this to remember the selected perturbation
		//        (when the same is ready for the updating mode)
		// perturbationstore = new PerturbationUser(graph, "NuSMV");
		updatePolicy = CFG_ASYNC; // Default update policy
	}

	public void setUpdatePolicy() {
		PriorityClassDefinition priorities = getPriorityClasses();
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

	public ObjectStore getStore() {
		return store;
	}

	public PriorityClassDefinition getPriorityClasses() {
		return (PriorityClassDefinition) store.getObject(0);
	}
	
	public void setPriorityClasses(PriorityClassDefinition pcdef) {
		store.setObject(0, pcdef);
	}

	public PerturbationHolder getPerturbation() {
		return perturbationstore;
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
}
