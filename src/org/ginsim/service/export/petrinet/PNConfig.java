package org.ginsim.service.export.petrinet;

import java.util.HashMap;
import java.util.Map;

import org.ginsim.graph.regulatorygraph.RegulatoryGraph;

import fr.univmrs.tagc.GINsim.regulatoryGraph.initialState.GsInitialStateStore;
import fr.univmrs.tagc.common.datastore.ObjectStore;

public class PNConfig implements GsInitialStateStore {

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
