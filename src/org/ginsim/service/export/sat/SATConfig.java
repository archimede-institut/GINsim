package org.ginsim.service.export.sat;

import java.util.HashMap;
import java.util.Map;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialState;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialStateStore;
import org.ginsim.core.graph.regulatorygraph.mutant.Perturbation;
import org.ginsim.core.utils.data.ObjectStore;


public class SATConfig implements InitialStateStore {

	public static final int CFG_FIX_POINT = 0;
	public static final int CFG_SCC = 1;
	
	private RegulatoryGraph graph;
	private Map<InitialState, Object> m_initStates;
	private Map<InitialState, Object> m_input;

	// Store has two objects: 0- Mutant
	public ObjectStore store = new ObjectStore(2);
	public Perturbation mutant;
	private int type;
	
	/**
	 * @param graph
	 */
	public SATConfig(RegulatoryGraph graph) {
		m_initStates = new HashMap<InitialState, Object>();
		m_input = new HashMap<InitialState, Object>();
		this.graph = graph;
		type = CFG_FIX_POINT;
	}

	public RegulatoryGraph getGraph() {
		return graph;
	}
	
	public Map<InitialState, Object> getInitialState() {
		return m_initStates;
	}

	public Map<InitialState, Object> getInputState() {
		return m_input;
	}
	
	public void setExportType(int type) {
		this.type = type;
	}

	public int getExportType() {
		return type;
	}
}

