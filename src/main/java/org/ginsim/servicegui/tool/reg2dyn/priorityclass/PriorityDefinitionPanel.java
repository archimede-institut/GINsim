package org.ginsim.servicegui.tool.reg2dyn.priorityclass;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import org.ginsim.common.application.Txt;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.gui.utils.data.*;
import org.ginsim.gui.utils.widgets.StockButton;
import org.ginsim.service.tool.reg2dyn.priorityclass.PriorityClass;
import org.ginsim.service.tool.reg2dyn.priorityclass.PrioritySetDefinition;
import org.ginsim.service.tool.reg2dyn.priorityclass.PrioritySetList;


/**
 * configure priority classes.
  */
public class PriorityDefinitionPanel extends ListEditionPanel<PriorityClass, PrioritySetDefinition>
        implements ListPanelCompanion<PrioritySetDefinition, PrioritySetList> {

    private final List<RegulatoryNode> nodeOrder;

    private PrioritySetList pcmanager;
    private PrioritySetDefinition pcdef;
    
    private final StockButton but_group = new StockButton("group.png",true);

    // TODO: make sure to always have at least one class selected

    /**
     * @param editPanel
     */
    public PriorityDefinitionPanel(ListEditionPanel<PrioritySetDefinition, PrioritySetList> editPanel) {
    	super(PriorityDefinitionHelper.HELPER, editPanel.getSelectedItem(), "pclassConfig", null, null);
        this.nodeOrder = editPanel.getList().nodeOrder;

        but_group.setMinimumSize(new Dimension(35,25));
        but_group.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                groupToggle();
            }
        });
        addButton(but_group);

        if (editPanel != null) {
            editPanel.addPanel(this, "PCLASS");
            editPanel.showPanel("PCLASS");
        }
    }

    public PrioritySetDefinition getCurrentDefinition() {
        return pcdef;
    }

    public List<RegulatoryNode> getNodeOrder() {
        return nodeOrder;
    }
    
    /**
     * call it when the user changes the class selection: update UI to match the selection
     */
    public void listSelectionUpdated(int[] sel) {
        super.listSelectionUpdated(sel);
        if (pcdef == null) {
            return;
        }

        int[][] selExtended = pcdef.getMovingRows(PrioritySetDefinition.NONE, sel);
        if (selExtended.length > 1) {
            but_group.setEnabled(true);
            but_group.setStockIcon("group.png");
            but_group.setToolTipText(Txt.t("STR_group_descr"));
        } else {
            if (selExtended.length == 0 || selExtended[0][0] == selExtended[0][1]) {
                but_group.setEnabled(false);
                but_group.setStockIcon("group.png");
                but_group.setToolTipText(Txt.t("STR_group_select_descr"));
            } else {
                but_group.setEnabled(true);
                but_group.setStockIcon("ungroup.png");
                but_group.setToolTipText(Txt.t("STR_ungroup_descr"));
            }
        }
    }

    /**
     * toggle the selection grouping:
     *    - if all selected items are part of the same group, it will be "ungrouped"
     *    - if selected items are part of several groups, they will be merged with the first one
     */
    protected void groupToggle() {
        pcdef.groupToggle(getSelection());
        refresh();
    }

	public void setEnabled(boolean b) {
        if (but_group == null) {
            return;
        }
		but_group.setEnabled(b);

        // TODO: forward setEnable to the companion panel
//        contentPanel.setEnabled(b);
//        availablePanel.setEnabled(b);
//		but_insert.setEnabled(b);
//		but_remove.setEnabled(b);
	}

    @Override
    public void setParentList(PrioritySetList list) {
        this.pcmanager = list;
    }

    @Override
    public void selectionUpdated(int[] selection) {
        PrioritySetDefinition curDef = pcdef;
        if (pcmanager == null || selection == null || selection.length != 1) {
            pcdef = null;
            if (curDef != null) {
                setList(null);
            }
            setEnabled(false);
            return;
        } else {
            pcdef = pcmanager.get(selection[0]);

            setEnabled(true);
        }
        if (curDef != pcdef) {
            setList(pcdef);
        }
    }

}
