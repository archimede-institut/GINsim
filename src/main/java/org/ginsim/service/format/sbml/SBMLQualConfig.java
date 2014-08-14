package org.ginsim.service.format.sbml;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.colomoto.logicalmodel.NodeInfo;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedState;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStateStore;
import org.ginsim.core.utils.data.ObjectStore;

public class SBMLQualConfig implements NamedStateStore {

    private final RegulatoryGraph graph;
	private Map<NamedState, Object> m_init = new HashMap();
	private Map<NamedState, Object> m_input = new HashMap();

	public SBMLQualConfig( RegulatoryGraph graph) {

		this.graph = graph;
	}

	public Map<NamedState, Object> getInitialState() {
		return m_init;
	}

	public Map getInputState() {
		return m_input;
	}

	public RegulatoryGraph getGraph() {
		return graph;
	}

    public NamedState getSelectedInitialState() {
        Iterator<NamedState> it = m_init.keySet().iterator();
        if (it.hasNext()) {
            return it.next();
        }
        return null;
    }

    public NamedState getSelectedInputState() {
        Iterator<NamedState> it = m_input.keySet().iterator();
        if (it.hasNext()) {
            return it.next();
        }
        return null;
    }
}
