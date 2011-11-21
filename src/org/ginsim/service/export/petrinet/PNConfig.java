package org.ginsim.service.export.petrinet;

import java.util.HashMap;
import java.util.Map;

import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.initialstate.InitialStateStore;

import fr.univmrs.tagc.common.datastore.ObjectStore;

public class PNConfig implements InitialStateStore {

	public final RegulatoryGraph graph;
	public final ObjectStore store = new ObjectStore(2);
	
    Map m_init = new HashMap();
    Map m_input = new HashMap();

	public PNConfig( RegulatoryGraph graph) {
		this.graph = graph;
	}
	
	public Map getInitialState() {
		return m_init;
	}

    public Map getInputState() {
        return m_input;
    }
}
