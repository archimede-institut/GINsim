package org.ginsim.service.tool.simulation;

import java.util.HashMap;
import java.util.Map;

import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStateStore;

public class SimulationParameter implements NamedStateStore {

    private Map m_initState = new HashMap();
    private Map m_input = new HashMap();

    
    
	@Override
	public Map getInitialState() {
		return m_initState;
	}

	@Override
	public Map getInputState() {
		return m_input;
	}

}
