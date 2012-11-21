package org.ginsim.gui.graph.regulatorygraph.perturbation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.ginsim.core.graph.regulatorygraph.perturbation.ListOfPerturbations;
import org.ginsim.core.graph.regulatorygraph.perturbation.Perturbation;
import org.ginsim.gui.utils.data.ListEditionPanel;
import org.ginsim.gui.utils.data.ListPanel;
import org.ginsim.gui.utils.data.ListPanelHelper;

/**
 * A panel to view, create and edit perturbations.
 *  
 * @author Aurelien Naldi
 */
public class PerturbationPanel extends JPanel {
	
	private final ListEditionPanel<Perturbation> spanel;
	private final ListPanel<Perturbation> mpanel;
	private final PerturbationPanelListHelper helper;
	private final MultipleListHelper mhelper;
	
	public PerturbationPanel(ListOfPerturbations perturbations) {
		
		super(new GridBagLayout());
		GridBagConstraints cst;
		
		helper = new PerturbationPanelListHelper(perturbations, this);
		spanel = new ListEditionPanel<Perturbation>(helper, perturbations.getSimplePerturbations(), "Perturbations");
		spanel.init();
		cst = new GridBagConstraints();
		cst.weightx = 1;
		cst.weighty = 0.5;
		cst.fill = GridBagConstraints.BOTH;
		add(spanel, cst);

		mhelper = new MultipleListHelper(perturbations);
		mpanel = new ListPanel<Perturbation>(mhelper, "Perturbations");
		mpanel.setList(perturbations.getMultiplePerturbations());
		cst = new GridBagConstraints();
		cst.gridy = 1;
		add(new JLabel("Combined perturbations"), cst);
		cst = new GridBagConstraints();
		cst.gridy = 2;
		cst.weightx = 1;
		cst.weighty = 1;
		cst.fill = GridBagConstraints.BOTH;
		add(mpanel, cst);
	}

	public void refresh() {
		mpanel.refresh();
		
	}
}

class MultipleListHelper extends ListPanelHelper<Perturbation> {
	
	ListOfPerturbations perturbations;
	
	public MultipleListHelper(ListOfPerturbations perturbations) {
		this.perturbations = perturbations;
		this.canAdd = false;
	}
	
	public boolean doRemove(int[] sel) {
		List<Perturbation> removed = new ArrayList<Perturbation>();
		List<Perturbation> multiples = perturbations.getMultiplePerturbations();
		for (int i=0 ; i< sel.length ; i++) {
			removed.add(perturbations.getMultiplePerturbations().get(sel[i]));
		}
		
		perturbations.removePerturbation(removed);
        return true;
	}

}
