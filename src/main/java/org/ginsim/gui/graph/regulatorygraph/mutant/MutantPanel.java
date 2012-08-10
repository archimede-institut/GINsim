package org.ginsim.gui.graph.regulatorygraph.mutant;

import java.awt.CardLayout;
import java.awt.Component;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.ginsim.core.graph.regulatorygraph.perturbation.Perturbation;
import org.ginsim.core.graph.regulatorygraph.perturbation.RegulatoryMutants;
import org.ginsim.gui.utils.data.ListEditionPanel;
import org.ginsim.gui.utils.data.ListPanel;
import org.ginsim.gui.utils.widgets.SplitPane;

public class MutantPanel extends ListEditionPanel<Perturbation> {

	public MutantPanel(RegulatoryMutants perturbations) {
		super(new PerturbationPanelListHelper(), perturbations.getSimplePerturbations(), "Perturbations");
		
		init();
	}

	
}

