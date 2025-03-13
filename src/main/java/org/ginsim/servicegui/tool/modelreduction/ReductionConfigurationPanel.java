package org.ginsim.servicegui.tool.modelreduction;

import org.colomoto.biolqm.NodeInfo;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.utils.data.*;
import org.ginsim.service.tool.modelreduction.ListOfReductionConfigs;
import org.ginsim.service.tool.modelreduction.ReductionConfig;
import org.ginsim.service.tool.modelreduction.ReductionConfigManager;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * The panel to configure reduction definitions.
 *
 * @author Aurelien Naldi
 */
public class ReductionConfigurationPanel extends ListEditionPanel<ReductionConfig, ListOfReductionConfigs> {

    private static final ReductionListHelper HELPER = new ReductionListHelper();

    public ReductionConfigurationPanel(RegulatoryGraph graph) {
        this((ListOfReductionConfigs)ObjectAssociationManager.getInstance().getObject(graph, ReductionConfigManager.KEY, true));
    }

    public ReductionConfigurationPanel(ListOfReductionConfigs cfgList) {
        super(HELPER, cfgList, "modelSimplifier", null, null);
    }
}


class ReductionListHelper extends ListPanelHelper<ReductionConfig, ListOfReductionConfigs> {

    public static final ColumnDefinition[] COLUMNS = {
            new ColumnDefinition(null,String.class, true),
    };

    public ReductionListHelper() {
        canOrder = true;
    }
    @Override
    public ColumnDefinition[] getColumns() {
        return COLUMNS;
    }

    @Override
    public int doCreate(ListOfReductionConfigs reductions, Object arg) {
        return reductions.create();
    }

    @Override
    public boolean doRemove(ListOfReductionConfigs reductions, int[] sel) {
        if (sel == null || sel.length < 1) {
            return false;
        }

        for (int i=sel.length-1 ; i>-1 ; i--) {
            reductions.remove( sel[i]);
        }
        return true;
    }

    @Override
    public ReductionPanelCompanion getCompanion(ListEditionPanel<ReductionConfig, ListOfReductionConfigs> editPanel) {
        return new ReductionPanelCompanion(editPanel);
    }
}

class ReductionPanelCompanion implements ListPanelCompanion<ReductionConfig, ListOfReductionConfigs> {

    private static final String EDIT = "edit";
    private static final String EMPTY = "empty";

    private ReductionConfigContentList ctlist = null;
    private final ListEditionPanel<ReductionConfig, ListOfReductionConfigs> editPanel;
    private final SimplifierConfigConfigurePanel panel;

    public ReductionPanelCompanion(ListEditionPanel<ReductionConfig, ListOfReductionConfigs> editPanel) {
        this.editPanel = editPanel;
        panel = new SimplifierConfigConfigurePanel();
        editPanel.addPanel(panel, EDIT);

        editPanel.addPanel(new JLabel("select or create a reduction"), EMPTY);
    }

    @Override
    public void setParentList(ListOfReductionConfigs reductions) {
        this.ctlist = new ReductionConfigContentList(reductions.getNodeOrder());
        panel.setList(ctlist);
    }

    @Override
    public void selectionUpdated(int[] selection) {
        if (ctlist == null) {
            return;
        }
        if (selection == null || selection.length < 1) {
            editPanel.showPanel(EMPTY);
        } else {
            panel.setConfig(editPanel.getSelectedItem());
            editPanel.showPanel(EDIT);
        }
    }
}


class SimplifierConfigConfigurePanel extends ListPanel<NodeInfo, ReductionConfigContentList> implements ChangeListener {

    // private final JCheckBox checkbox;
    // private final JCheckBox cb_propagate;
    //private final JCheckBox cb_outputs;
    private ReductionConfig config;

	SimplifierConfigConfigurePanel() {
        super(ReductionConfigHelper.HELPER, "Selected");

        JPanel optionPanel = new JPanel( new GridBagLayout() );
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
        // this.cb_propagate = new JCheckBox("Propagate fixed values");
        // this.cb_outputs  = new JCheckBox("Strip (pseudo-)outputs");
		// this.checkbox = new JCheckBox("Strict (do not try to remove self-regulated nodes)");
        // optionPanel.add(this.cb_propagate, c);
        // c.gridy++;
        // optionPanel.add(this.cb_outputs, c);
        // c.gridy++;
        // optionPanel.add(this.checkbox, c);

        c.gridy = 0;
        add(optionPanel, c);

        // this.cb_propagate.addChangeListener(this);
        // this.cb_outputs.addChangeListener(this);
        // this.checkbox.addChangeListener(this);

	}

    public void setConfig(ReductionConfig config) {
        this.config = config;
        if (list != null) {
            list.setConfig(config);
        }
        if (config == null) {
            return;
        }

        boolean propagate = config.propagate;
        boolean outputs = config.outputs;
        boolean strict = config.strict;

        // cb_propagate.setSelected( propagate );
        // cb_outputs.setSelected( outputs );
        // checkbox.setSelected( strict );
    }

    @Override
	public void stateChanged(ChangeEvent e) {
        if (config != null) {
            // config.propagate = cb_propagate.isSelected();
            // config.outputs = cb_outputs.isSelected();
            // config.strict = checkbox.isSelected();
        }
	}
}

class ReductionConfigHelper extends ListPanelHelper<NodeInfo, ReductionConfigContentList> {

    public static final ReductionConfigHelper HELPER = new ReductionConfigHelper();

    public static final ColumnDefinition[] COLUMNS = {
            new ColumnDefinition(null,String.class, false),
            new ColumnDefinition(null,Boolean.class, true),
    };

    private ReductionConfigHelper() {
        // private constructor
    }

    @Override
    public ColumnDefinition[] getColumns() {
        return COLUMNS;
    }

    @Override
    public Object getValue(ReductionConfigContentList list, NodeInfo node, int column) {
        if (column == 0) {
            return node;
        }

        if (column == 1) {
            return list.isSelected(node);
        }

        return null;
    }

    public boolean setValue(ReductionConfigContentList list, int row, int column, Object value) {
        if (column != 1) {
            return false;
        }

        if (value instanceof Boolean) {
            NodeInfo node = list.get(row);
            return list.setSelected(node, (Boolean)value);
        }

        return false;
    }

}
