package org.ginsim.service.export.petrinet;

import java.util.HashMap;
import java.util.Map;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialStateStore;
import org.ginsim.core.utils.data.ObjectStore;


public class PNConfig implements InitialStateStore {

	public final RegulatoryGraph graph;
	public final ObjectStore store = new ObjectStore(2);
	public BasePetriNetExport format;
	
    Map m_init = new HashMap();
    Map m_input = new HashMap();

	public PNConfig( RegulatoryGraph graph) {
		this.graph = graph;
		format = BasePetriNetExport.FORMATS.get(0);
	}
	
	public Map getInitialState() {
		return m_init;
	}

    public Map getInputState() {
        return m_input;
    }
}
