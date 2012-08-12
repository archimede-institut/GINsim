package org.ginsim.gui.graph.regulatorygraph.mutant;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.perturbation.Perturbation;
import org.ginsim.core.graph.regulatorygraph.perturbation.RegulatoryMutants;
import org.ginsim.gui.utils.data.ListEditionPanel;

public class MutantPanel extends ListEditionPanel<Perturbation> {

	public MutantPanel(RegulatoryMutants perturbations) {
		super(new PerturbationPanelListHelper(perturbations), perturbations.getSimplePerturbations(), "Perturbations");
		
		init();
	}

	
}

