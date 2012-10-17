package org.ginsim.service.export.petrinet;

import java.util.HashMap;
import java.util.Map;

import org.colomoto.logicalmodel.LogicalModel;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialStateStore;
import org.ginsim.core.graph.regulatorygraph.perturbation.Perturbation;
import org.ginsim.core.graph.regulatorygraph.perturbation.PerturbationHolder;
import org.ginsim.core.utils.data.ObjectStore;
import org.ginsim.service.tool.reg2dyn.priorityclass.PriorityClassDefinition;


public class PNConfig implements InitialStateStore {

	public PNFormat format;
	public PriorityClassDefinition priorities = null;
    Map m_init = new HashMap();
    Map m_input = new HashMap();

	public PNConfig( ) {
		// FIXME: change default when the format choice is restored
		format = new PetriNetExportINA();
	}
	
	public Map getInitialState() {
		return m_init;
	}

    public Map getInputState() {
        return m_input;
    }

}
