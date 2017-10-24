package org.ginsim.servicegui.tool.reg2dyn.priorityclass;

import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.gui.utils.data.ListEditionPanel;
import org.ginsim.gui.utils.data.ListPanelCompanion;
import org.ginsim.service.tool.reg2dyn.priorityclass.PrioritySetDefinition;
import org.ginsim.service.tool.reg2dyn.priorityclass.PrioritySetList;
import org.ginsim.service.tool.reg2dyn.updater.UpdaterDefinition;

@SuppressWarnings("serial")
public class UpdatingPanel extends JPanel implements ListPanelCompanion<UpdaterDefinition, PrioritySetList> {

    private final List<RegulatoryNode> nodeOrder;
	PrioritySetList pcmanager;
	UpdaterDefinition updater;
	PriorityDefinitionPanel priorityPanel = null;
	ListEditionPanel<UpdaterDefinition, PrioritySetList> editPanel;
	
	JLabel info = new JLabel();
	
    public UpdatingPanel(ListEditionPanel<UpdaterDefinition, PrioritySetList> editPanel) {
    	this.editPanel = editPanel;
    	this.nodeOrder = editPanel.getList().nodeOrder;

    	add(info);
        editPanel.addPanel(this, "INFO");
	}

	@Override
    public void setParentList(PrioritySetList list) {
        this.pcmanager = list;
    }

    @Override
    public void selectionUpdated(int[] selection) {
    	UpdaterDefinition curDef = updater;
        if (pcmanager == null || selection == null || selection.length != 1) {
            updater = null;
            if (curDef != null) {
                setList(null);
            }
            setEnabled(false);
            return;
        } else {
            updater = pcmanager.get(selection[0]);

            setEnabled(true);
        }
        if (curDef != updater) {
            setList(updater);
        }
    }

	private void setList(UpdaterDefinition updater) {
		this.updater = updater;
		
		if (updater instanceof PrioritySetDefinition) {
			if (priorityPanel == null) {
				priorityPanel = new PriorityDefinitionPanel(nodeOrder, (PrioritySetDefinition)updater);				
	            editPanel.addPanel(priorityPanel, "PCLASS");
			}
			priorityPanel.setList((PrioritySetDefinition)updater);
            editPanel.showPanel("PCLASS");
		} else {
			if (updater == null) {
				info.setText("Nothing to show");
			} else {
				info.setText(updater.toString());
			}
            editPanel.showPanel("INFO");
		}
		// TODO: show information and dedicated edition panel
	}
}
