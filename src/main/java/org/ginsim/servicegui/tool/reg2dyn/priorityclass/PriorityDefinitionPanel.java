package org.ginsim.servicegui.tool.reg2dyn.priorityclass;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.ginsim.common.application.Translator;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.gui.utils.data.*;
import org.ginsim.gui.utils.widgets.StockButton;
import org.ginsim.service.tool.reg2dyn.priorityclass.PriorityClassDefinition;
import org.ginsim.service.tool.reg2dyn.priorityclass.PriorityClassManager;
import org.ginsim.service.tool.reg2dyn.priorityclass.Reg2dynPriorityClass;
import org.ginsim.servicegui.tool.reg2dyn.priorityclass.PriorityDefinitionHelper;


/**
 * configure priority classes.
  */
public class PriorityDefinitionPanel extends ListEditionPanel<Reg2dynPriorityClass, PriorityClassDefinition>
        implements ListPanelCompanion<PriorityClassDefinition, PriorityClassManager> {

    private static final int UP = PriorityClassDefinition.UP;
    private static final int DOWN = PriorityClassDefinition.DOWN;
    private static final int NONE = PriorityClassDefinition.NONE;
    
    private List<RegulatoryNode> nodeOrder;

    PriorityClassManager pcmanager;
    PriorityClassDefinition pcdef;
    
    private final StockButton but_group = new StockButton("group.png",true);

//	private final ListEditionPanel<PriorityClassDefinition, PriorityClassManager> parentPanel;


    // TODO: make sure to always have at least one class selected



    /**
     * @param editPanel
     */
    public PriorityDefinitionPanel(ListEditionPanel<PriorityClassDefinition, PriorityClassManager> editPanel) {
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

    public PriorityClassDefinition getCurrentDefinition() {
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

        int[][] selExtended = pcdef.getMovingRows(NONE, sel);
        if (selExtended.length > 1) {
            but_group.setEnabled(true);
            but_group.setStockIcon("group.png");
            but_group.setToolTipText(Translator.getString("STR_group_descr"));
        } else {
            if (selExtended.length == 0 || selExtended[0][0] == selExtended[0][1]) {
                but_group.setEnabled(false);
                but_group.setStockIcon("group.png");
                but_group.setToolTipText(Translator.getString("STR_group_select_descr"));
            } else {
                but_group.setEnabled(true);
                but_group.setStockIcon("ungroup.png");
                but_group.setToolTipText(Translator.getString("STR_ungroup_descr"));
            }
        }
    }

    /**
     * toggle the selection grouping:
     *    - if all selected items are part of the same group, it will be "ungrouped"
     *    - if selected items are part of several groups, they will be merged with the first one
     */
    protected void groupToggle() {
        int[] ts = getSelection();
        int[][] selExtended = pcdef.getMovingRows(NONE, ts);
        // if class with different priorities are selected: give them all the same priority
        if (selExtended.length < 1) {
        	return;
        }
        if (selExtended.length > 1) {
            if (ts == null || ts.length < 1) {
                return;
            }
            int pos = selExtended[0][1];
            int pr = pcdef.get(pos).rank;
            for (int i=1 ; i<selExtended.length ; i++) {
            	for (int j=selExtended[i-1][1]+1 ; j<selExtended[i][0] ; j++) {
            		pcdef.get(j).rank -= i-1;
            	}
            	for (int j=selExtended[i][0] ; j<=selExtended[i][1] ; j++) {
	                pos++;
	                pcdef.get(j).rank = pr;
	                pcdef.moveElementAt(j, pos);
            	}
            }
            int l = selExtended.length - 1;
            for (int j=selExtended[l][1]+1 ; j<pcdef.size() ; j++) {
            	pcdef.get(j).rank -= l;
            }
            pcdef.refresh();
            // FIXME: select first item
//            getSelectionModel().clearSelection();
//            getSelectionModel().addSelectionInterval(selExtended[0][0],pos);
        } else {
            if (selExtended[0][0] != selExtended[0][1]) {
                int i = selExtended[0][0];
                int inc = 1;
                for (i++ ; i<selExtended[0][1] ; i++) {
                    pcdef.get(i).rank += inc;
                    inc++;
                }
                for ( ; i<pcdef.size() ; i++) {
                    pcdef.get(i).rank += inc;
                }
                pcdef.refresh();
            }
        }

        // TODO: refresh list view
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
    public void setParentList(PriorityClassManager list) {
        this.pcmanager = list;
    }

    @Override
    public void selectionUpdated(int[] selection) {
        PriorityClassDefinition curDef = pcdef;
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
