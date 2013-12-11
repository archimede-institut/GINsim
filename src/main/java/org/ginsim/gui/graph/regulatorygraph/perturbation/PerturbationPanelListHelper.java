package org.ginsim.gui.graph.regulatorygraph.perturbation;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.ginsim.core.graph.regulatorygraph.perturbation.ListOfPerturbations;
import org.ginsim.core.graph.regulatorygraph.perturbation.Perturbation;
import org.ginsim.gui.utils.data.ListEditionPanel;
import org.ginsim.gui.utils.data.ListPanelHelper;

public class PerturbationPanelListHelper extends ListPanelHelper<Perturbation> {

	private static final String CREATE = "CREATE";
	private static final String SELECTION = "SELECTION";
	
	private final ListOfPerturbations perturbations;
	
	private MultipleSelectionPanel selectionPanel = null;
	
	private PerturbationCreatePanel createPanel = null;
	private final PerturbationPanel perturbationPanel;
	
	public PerturbationPanelListHelper(ListOfPerturbations perturbations, PerturbationPanel perturbationPanel) {
		this.perturbations = perturbations;
		this.perturbationPanel = perturbationPanel;
	}
	
	@Override
	public int doCreate(Object arg) {
		if (editPanel == null) {
			return -1;
		}
		if (createPanel == null) {
			createPanel = new PerturbationCreatePanel(this, perturbations);
			editPanel.addPanel(createPanel, CREATE);
		}
		editPanel.showPanel(CREATE);
		return -1;
	}

	@Override
	public void fillEditPanel() {
		if (editPanel == null) {
			return;
		}
		if (selectionPanel == null) {
			selectionPanel = new MultipleSelectionPanel(this);
			editPanel.addPanel(selectionPanel, SELECTION);
		}
	}
	
	public void selectionUpdated(int[] selection) {
		if (selectionPanel == null) {
			return;
		}
		if (selection == null || selection.length < 1) {
			create(null);
			return;
		}
		
		if (selection.length == 1) {
			selectionPanel.select(selection[0]);
		} else {
			selectionPanel.select(perturbationPanel, perturbations, selection);
		}
		editPanel.showPanel(SELECTION);
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

class MultipleSelectionPanel extends JPanel {

	private final PerturbationPanelListHelper helper;
	JButton btn = new JButton();
	JLabel label = new JLabel();
	
	public MultipleSelectionPanel(PerturbationPanelListHelper helper) {
		this.helper = helper;
		add(label);
		add(btn);
		select(-1);
	}

	public void select(int index) {
		btn.setEnabled(false);
		btn.setVisible(false);
		if (index < 0) {
			label.setText("No selection");
		} else {
			label.setText("Selected: "+index + ". TODO: show info");
		}
	}

	public void select(PerturbationPanel perturbationPanel, ListOfPerturbations perturbations, int[] indices) {
		label.setText("Selected: "+indices.length + " perturbations. TODO: show info");
		Action createAction = new AddMultiplePerturbationAction(perturbationPanel, perturbations, indices);
		btn.setAction(createAction);
		btn.setEnabled(true);
		btn.setVisible(true);
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
