package org.ginsim.service.export.nusmv;

import java.util.HashMap;
import java.util.Map;

import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.initialstate.InitialStateStore;
import org.ginsim.graph.regulatorygraph.mutant.RegulatoryMutantDef;
import org.ginsim.gui.service.tools.reg2dyn.PriorityClassDefinition;

import fr.univmrs.tagc.common.datastore.ObjectStore;

public class GsNuSMVConfig implements InitialStateStore {

	public static final int CFG_SYNC = 0;
	public static final int CFG_ASYNC = 1;
	public static final int CFG_PCLASS = 2;
	public static final int CFG_INPUT_FRONZEN = 10;
	public static final int CFG_INPUT_IVAR = 11;

	RegulatoryGraph graph;
	Map m_initStates;
	Map m_input;
	// Store has two objects: 0- Mutant & 1- PriorityClass
	ObjectStore store = new ObjectStore(2);
	public RegulatoryMutantDef mutant;
	private int updatePolicy;
	private int exportType;

	/**
	 * @param graph
	 */
	public GsNuSMVConfig(RegulatoryGraph graph) {
		m_initStates = new HashMap();
		m_input = new HashMap();
		this.graph = graph;
		updatePolicy = CFG_ASYNC; // Default update policy
		exportType = CFG_INPUT_FRONZEN; // Default export type
	}
	
	public void setUpdatePolicy() {
		PriorityClassDefinition priorities = (PriorityClassDefinition) store
				.getObject(1);
		if (priorities == null)
			updatePolicy = CFG_ASYNC;
		else if (priorities.getNbElements() == 1) {
			if (priorities.getPclass(graph.getNodeOrder())[0][1] == 0)
				updatePolicy = CFG_SYNC;
			else
				updatePolicy = CFG_ASYNC;
		} else
			updatePolicy = CFG_PCLASS;
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

	public Map getInitialState() {
		return m_initStates;
	}

	public Map getInputState() {
		return m_input;
	}
}

