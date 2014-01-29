package org.ginsim.service.format.sbml;

import java.util.HashMap;
import java.util.Map;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStateStore;
import org.ginsim.core.utils.data.ObjectStore;

public class SBMLQualConfig implements NamedStateStore {
	
	Map m_init = new HashMap();
	Map m_input = new HashMap();
	ObjectStore store = new ObjectStore(2);
	RegulatoryGraph graph;
	
	public SBMLQualConfig( RegulatoryGraph graph) {
		
		this.graph = graph;
	}

	public Map getInitialState() {
		return m_init;
	}

	public Map getInputState() {
		return m_input;
	}
	
	public RegulatoryGraph getGraph() {
		return graph;
	}
}
