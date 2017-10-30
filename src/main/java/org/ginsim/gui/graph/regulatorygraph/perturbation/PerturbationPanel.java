package org.ginsim.gui.graph.regulatorygraph.perturbation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JPanel;

import org.ginsim.core.graph.regulatorygraph.perturbation.ListOfPerturbations;
import org.ginsim.core.graph.regulatorygraph.perturbation.Perturbation;
import org.ginsim.gui.utils.data.ListEditionPanel;

/**
 * A panel to view, create and edit perturbations.
 *  
 * @author Aurelien Naldi
 */
@SuppressWarnings("serial")
public class PerturbationPanel extends JPanel {
	
	private final ListEditionPanel<Perturbation, ListOfPerturbations> spanel;
	private final PerturbationPanelListHelper helper;

	public PerturbationPanel(ListOfPerturbations perturbations) {
		
		super(new GridBagLayout());
		GridBagConstraints cst = new GridBagConstraints();;
		helper = PerturbationPanelListHelper.getHelper();
		spanel = new ListEditionPanel<Perturbation, ListOfPerturbations>(helper, perturbations, "Perturbations", null, this);
		cst.weightx = 1;
		cst.weighty = 0.5;
		cst.fill = GridBagConstraints.BOTH;
		add(spanel, cst);

	}

}
