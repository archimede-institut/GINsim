package org.ginsim.gui.graph.regulatorygraph.perturbation;

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
import org.ginsim.gui.utils.data.ListPanelCompanion;
import org.ginsim.gui.utils.data.ListPanelHelper;

public class PerturbationPanelListHelper extends ListPanelHelper<Perturbation, ListOfPerturbations> {

    private static final PerturbationPanelListHelper HELPER = new PerturbationPanelListHelper();

    public static PerturbationPanelListHelper getHelper() {
        return HELPER;
    }

    private PerturbationPanelListHelper() {
        // private constructor
    }

    @Override
    public boolean doRemove(ListOfPerturbations list, int[] sel) {
        List<Perturbation> to_remove = new ArrayList<Perturbation>();
        for (int idx: sel) {
            to_remove.add(list.get(idx));
        }
        list.removePerturbations(to_remove);
        return true;
    }

    @Override
    public PerturbationPanelCompanion getCompanion(ListEditionPanel<Perturbation, ListOfPerturbations> editPanel) {
        return new PerturbationPanelCompanion(editPanel);
    }
}


class PerturbationPanelCompanion implements ListPanelCompanion<Perturbation, ListOfPerturbations> {

    private static final String CREATE = "CREATE";
    private static final String SELECTION = "SELECTION";

    private ListOfPerturbations perturbations = null;

    private final ListEditionPanel<Perturbation, ListOfPerturbations> editPanel;

    private PerturbationCreatePanel createPanel = null;
    private MultipleSelectionPanel selectionPanel = null;

	public PerturbationPanelCompanion(ListEditionPanel<Perturbation, ListOfPerturbations> editPanel) {
        this.editPanel = editPanel;

        if (editPanel != null) {
            if (selectionPanel == null) {
                selectionPanel = new MultipleSelectionPanel(this);
                editPanel.addPanel(selectionPanel, SELECTION);
            }
        }
        selectionUpdated(null);
    }

    public int create(ListOfPerturbations perturbations, Object arg) {
        if (editPanel == null) {
            return -1;
        }
        editPanel.showPanel(CREATE);

        refresh();

        return -1;
    }

    @Override
    public void setList(ListOfPerturbations perturbations) {
        this.perturbations = perturbations;
        if (perturbations != null && createPanel == null) {
            createPanel = new PerturbationCreatePanel(this, this.perturbations);
            editPanel.addPanel(createPanel, CREATE);
        }
        refresh();
    }

    @Override
    public void selectionUpdated(int[] selection) {
        if (selectionPanel == null) {
            return;
        }
        if (selection == null || selection.length < 1) {
            if (editPanel != null) {
                editPanel.showPanel(CREATE);
                refresh();
            }
            return;
        }

        if (selection.length == 1) {
            selectionPanel.select(selection[0]);
        } else {
            selectionPanel.select(perturbations, selection);
        }
        editPanel.showPanel(SELECTION);
    }

    public void refresh() {
        if (editPanel != null) {
            editPanel.refresh();
        }
    }

    public void addMultiple(int[] selected) {
        List<Perturbation> lselected = new ArrayList<Perturbation>();
        for (int i: selected) {
            lselected.add(perturbations.get(i));
        }
        perturbations.addMultiplePerturbation(lselected);
        refresh();
    }

    public boolean doRemove(int[] sel) {
        // TODO: smart removal

        List<Perturbation> removed = new ArrayList<Perturbation>();
        for (int i=0 ; i< sel.length ; i++) {
            removed.add(perturbations.get(sel[i]));
        }

        perturbations.removePerturbations(removed);
        return true;
    }
}

class MultipleSelectionPanel extends JPanel {

	private final PerturbationPanelCompanion companion;
	JButton btn = new JButton();
	JLabel label = new JLabel();
	
	public MultipleSelectionPanel(PerturbationPanelCompanion companion) {
		this.companion = companion;
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

	public void select(ListOfPerturbations perturbations, int[] indices) {
		label.setText("Selected: "+indices.length + " perturbations. TODO: show info");
		Action createAction = new AddMultiplePerturbationAction(companion, indices);
		btn.setAction(createAction);
		btn.setEnabled(true);
		btn.setVisible(true);
	}
}


class AddMultiplePerturbationAction extends AbstractAction {
	
	private final PerturbationPanelCompanion companion;
	private final int[] selected;
	
	public AddMultiplePerturbationAction(PerturbationPanelCompanion companion, int[] selected) {
		super("Create multiple perturbation");
		this.companion = companion;
		this.selected = selected;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
        companion.addMultiple(selected);
	}
}
