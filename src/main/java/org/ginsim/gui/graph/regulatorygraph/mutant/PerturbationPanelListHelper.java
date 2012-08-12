package org.ginsim.gui.graph.regulatorygraph.mutant;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.perturbation.Perturbation;
import org.ginsim.core.graph.regulatorygraph.perturbation.RegulatoryMutants;
import org.ginsim.gui.utils.data.ListPanelHelper;

public class PerturbationPanelListHelper extends ListPanelHelper<Perturbation> {

	private static final String CREATE = "CREATE";
	
	private final RegulatoryMutants perturbations;
	
	private JLabel selectedLabel = null;
	private JButton multipleLabel = null;
	
	private PerturbationCreatePanel createPanel = null;
	
	public PerturbationPanelListHelper(RegulatoryMutants perturbations) {
		this.perturbations = perturbations;
	}
	
	public Object[] getCreateTypes() {
		
		return PerturbationType.values();
	}

	public int create(Object arg) {
		if (arg instanceof PerturbationType) {
			PerturbationType type = (PerturbationType)arg;
			
			if (createPanel == null) {
				createPanel = new PerturbationCreatePanel(null);
				editPanel.addPanel(createPanel, CREATE);
			}
			createPanel.setType(type);
			editPanel.showPanel(CREATE);
		}
		
		return -1;
	}
	
	public Component getSingleSelectionPanel() {
		if (selectedLabel == null) {
			selectedLabel = new JLabel();
		}
		return selectedLabel;
	}
	
	public Component getMultipleSelectionPanel() {
		if (multipleLabel == null) {
			multipleLabel = new JButton("TODO: multiple");
		}
		return multipleLabel;
	}

	public void updateSelectionPanel(int index) {
		selectedLabel.setText("selected: "+index);
	}
	public void updateMultipleSelectionPanel(int[] indices) {
		multipleLabel.setAction(new AddMultiplePerturbationAction(perturbations, indices));
	}

}

class AddMultiplePerturbationAction extends AbstractAction {
	
	private final RegulatoryMutants perturbations;
	private final int[] selected;
	
	public AddMultiplePerturbationAction(RegulatoryMutants perturbations, int[] selected) {
		super("Create multiple perturbation");
		this.perturbations = perturbations;
		this.selected = selected;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		List<Perturbation> lselected = new ArrayList<Perturbation>();
		for (int i: selected) {
			lselected.add(perturbations.get(i));
		}
		perturbations.addMultiplePerturbation(lselected);
	}

}

