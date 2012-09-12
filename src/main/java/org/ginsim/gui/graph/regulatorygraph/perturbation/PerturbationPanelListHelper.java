package org.ginsim.gui.graph.regulatorygraph.perturbation;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;

import org.ginsim.core.graph.regulatorygraph.perturbation.ListOfPerturbations;
import org.ginsim.core.graph.regulatorygraph.perturbation.Perturbation;
import org.ginsim.gui.utils.data.ListPanelHelper;

public class PerturbationPanelListHelper extends ListPanelHelper<Perturbation> {

	private static final String CREATE = "CREATE";
	
	private final ListOfPerturbations perturbations;
	
	private JLabel selectedLabel = null;
	private JButton multipleLabel = null;
	
	private PerturbationCreatePanel createPanel = null;
	private final PerturbationPanel perturbationPanel;
	
	public PerturbationPanelListHelper(ListOfPerturbations perturbations, PerturbationPanel perturbationPanel) {
		this.perturbations = perturbations;
		this.perturbationPanel = perturbationPanel;
	}
	
	public Object[] getCreateTypes() {
		
		return PerturbationType.values();
	}

	public int create(Object arg) {
		if (arg instanceof PerturbationType) {
			PerturbationType type = (PerturbationType)arg;
			
			if (createPanel == null) {
				createPanel = new PerturbationCreatePanel(this, perturbations);
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
		multipleLabel.setAction(new AddMultiplePerturbationAction(perturbationPanel, perturbations, indices));
	}

	@Override
	public boolean doRemove(int[] sel) {
		List<Perturbation> removed = new ArrayList<Perturbation>();
		for (int i=0 ; i< sel.length ; i++) {
			removed.add(perturbations.get(sel[i]));
		}
		
		perturbations.removePerturbation(removed);
        return true;
	}

}

class AddMultiplePerturbationAction extends AbstractAction {
	
	private final ListOfPerturbations perturbations;
	private final PerturbationPanel panel;
	private final int[] selected;
	
	public AddMultiplePerturbationAction(PerturbationPanel panel, ListOfPerturbations perturbations, int[] selected) {
		super("Create multiple perturbation");
		this.perturbations = perturbations;
		this.panel = panel;
		this.selected = selected;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		List<Perturbation> lselected = new ArrayList<Perturbation>();
		for (int i: selected) {
			lselected.add(perturbations.get(i));
		}
		perturbations.addMultiplePerturbation(lselected);
		panel.refresh();
	}

}

