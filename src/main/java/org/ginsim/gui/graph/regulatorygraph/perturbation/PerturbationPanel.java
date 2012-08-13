package org.ginsim.gui.graph.regulatorygraph.perturbation;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.perturbation.Perturbation;
import org.ginsim.core.graph.regulatorygraph.perturbation.ListOfPerturbations;
import org.ginsim.gui.utils.data.ListEditionPanel;

/**
 * A panel to view, create and edit perturbations.
 *  
 * @author Aurelien Naldi
 */
public class PerturbationPanel extends ListEditionPanel<Perturbation> {

	public PerturbationPanel(ListOfPerturbations perturbations) {
		super(new PerturbationPanelListHelper(perturbations), perturbations.getSimplePerturbations(), "Perturbations");
		
		init();
	}

}

