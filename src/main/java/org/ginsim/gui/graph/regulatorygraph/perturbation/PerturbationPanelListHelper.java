package org.ginsim.gui.graph.regulatorygraph.perturbation;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

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

    protected ListOfPerturbations perturbations = null;

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
    public void setParentList(ListOfPerturbations perturbations) {
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

	public void showCreatePanel() {
        editPanel.showPanel(CREATE);
        refresh();
	}
}

class MultipleSelectionPanel extends JPanel {

	private final PerturbationPanelCompanion companion;
	AddMultiplePerturbationAction createAction;
	AddPerturbationAction showCreateAction;
	JTextArea label = new JTextArea();

	
	public MultipleSelectionPanel(PerturbationPanelCompanion companion) {
		super(new GridBagLayout());
		this.companion = companion;

		GridBagConstraints cst = new GridBagConstraints();
		Insets insets = new Insets(5, 5, 10, 10);
		cst.insets = insets;
		cst.gridx = cst.gridy = 0;
        
        cst.gridy = 1;
        createAction = new AddMultiplePerturbationAction(companion);
		add(new JButton(createAction), cst);
		cst.gridx = 1;
		showCreateAction = new AddPerturbationAction(companion);
        add(new JButton(showCreateAction), cst);

        JScrollPane sp = new JScrollPane();
		label.setEditable(false);
        sp.setViewportView(label);
		cst.gridx = 0;
        cst.gridy = 0;
        cst.gridwidth = 2;
        cst.weightx = 1;
        cst.weighty = 1;
        cst.fill = GridBagConstraints.BOTH;
		add(sp, cst);
		select(-1);
	}

	public void select(int index) {
		createAction.setSelection(null);
		if (index < 0) {
			label.setText("No selection");
		} else {
            Perturbation p = companion.perturbations.get(index);
            if (p != null) {
                label.setText(p.getDescription());
            }
		}
	}

	public void select(ListOfPerturbations perturbations, int[] indices) {
        StringBuffer sb = new StringBuffer(indices.length + " perturbations are selected:");
        for (int index: indices) {
            String description = companion.perturbations.get(index).getDescription();
            boolean firstline = true;
            for (String line: description.split("\n")) {
                if (firstline) {
                    sb.append("\n* ");
                    firstline = false;
                } else {
                    sb.append("\n  ");
                }
                sb.append(line);
            }
        }
		label.setText(sb.toString());
		createAction.setSelection(indices);
	}
}


class AddMultiplePerturbationAction extends AbstractAction {
	
	private final PerturbationPanelCompanion companion;
	private int[] selected;
	
	public AddMultiplePerturbationAction(PerturbationPanelCompanion companion) {
		super("Create multiple perturbation");
		this.companion = companion;
		this.selected = null;
	}
	
	public void setSelection(int[] indices) {
		this.selected = indices;
		setEnabled(indices != null && indices.length > 1);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
        companion.addMultiple(selected);
	}
}

class AddPerturbationAction extends AbstractAction {
	
	private final PerturbationPanelCompanion companion;
	
	public AddPerturbationAction(PerturbationPanelCompanion companion) {
		super("Setup new Perturbation");
		this.companion = companion;
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
        companion.showCreatePanel();
	}
}
