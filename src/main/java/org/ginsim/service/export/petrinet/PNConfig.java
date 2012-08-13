package org.ginsim.service.export.petrinet;

import java.util.HashMap;
import java.util.Map;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialStateStore;
import org.ginsim.core.graph.regulatorygraph.perturbation.Perturbation;
import org.ginsim.core.graph.regulatorygraph.perturbation.PerturbationHolder;
import org.ginsim.core.utils.data.ObjectStore;


public class PNConfig implements InitialStateStore, PerturbationHolder {

	public final RegulatoryGraph graph;
	public final ObjectStore store = new ObjectStore(2);
	public final BasePetriNetExport format;
	private Perturbation perturbation;
	
    Map m_init = new HashMap();
    Map m_input = new HashMap();

	public PNConfig( RegulatoryGraph graph) {
		this.graph = graph;
		format = PetrinetExportService.FORMATS.get(0);
	}
	
	public Map getInitialState() {
		return m_init;
	}

    public Map getInputState() {
        return m_input;
    }

	@Override
	public Perturbation getPerturbation() {
		return perturbation;
	}

	@Override
	public void setPerturbation(Perturbation p) {
		this.perturbation = perturbation;
	}
}
