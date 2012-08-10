package org.ginsim.gui.graph.regulatorygraph.mutant;

import java.util.List;

import javax.swing.JLabel;

import org.ginsim.core.graph.regulatorygraph.perturbation.Perturbation;
import org.ginsim.core.graph.regulatorygraph.perturbation.RegulatoryMutants;
import org.ginsim.gui.utils.data.ListPanel;
import org.ginsim.gui.utils.widgets.SplitPane;

public class MutantPanel extends SplitPane {

	public MutantPanel(RegulatoryMutants perturbations) {
		
		PerturbationPanelListHelper helper = new PerturbationPanelListHelper();
		ListPanel<Perturbation> listPanel = new ListPanel<Perturbation>(helper, "Perturbations");
		listPanel.setList(perturbations.getSimplePerturbations());
		
		setLeftComponent( listPanel);
		setRightComponent(new JLabel("TODO: perturbation editor in progress"));
	}
}
