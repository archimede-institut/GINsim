package org.ginsim.servicegui.tool.modelsimplifier;

import org.colomoto.logicalmodel.NodeInfo;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.utils.data.*;
import org.ginsim.service.tool.modelsimplifier.ModelSimplifierConfig;
import org.ginsim.service.tool.modelsimplifier.ModelSimplifierConfigList;
import org.ginsim.service.tool.modelsimplifier.ModelSimplifierConfigManager;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * The panel to configure reduction definitions.
 *
 * @author Aurelien Naldi
 */
public class ReductionConfigurationPanel extends ListEditionPanel<ModelSimplifierConfig, ModelSimplifierConfigList> {

    private static final ReductionListHelper HELPER = new ReductionListHelper();

    public ReductionConfigurationPanel(RegulatoryGraph graph) {
        this((ModelSimplifierConfigList)ObjectAssociationManager.getInstance().getObject(graph, ModelSimplifierConfigManager.KEY, true));
    }

    public ReductionConfigurationPanel(ModelSimplifierConfigList cfgList) {
        super(HELPER, cfgList, "modelSimplifier", null, null);
    }
}


class ReductionListHelper extends ListPanelHelper<ModelSimplifierConfig, ModelSimplifierConfigList> {

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
    public int doCreate(ModelSimplifierConfigList reductions, Object arg) {
        return reductions.create();
    }

    @Override
    public boolean doRemove(ModelSimplifierConfigList reductions, int[] sel) {
        if (sel == null || sel.length < 1) {
            return false;
        }

        for (int i=sel.length-1 ; i>-1 ; i--) {
            reductions.remove( sel[i]);
        }
        return true;
    }

    @Override
    public ReductionPanelCompanion getCompanion(ListEditionPanel<ModelSimplifierConfig, ModelSimplifierConfigList> editPanel) {
        return new ReductionPanelCompanion(editPanel);
    }
}

class ReductionPanelCompanion implements ListPanelCompanion<ModelSimplifierConfig, ModelSimplifierConfigList> {

    private static final String EDIT = "edit";
    private static final String EMPTY = "empty";

    private SimplifierConfigContentList ctlist = null;
    private final ListEditionPanel<ModelSimplifierConfig, ModelSimplifierConfigList> editPanel;
    private final SimplifierConfigConfigurePanel panel;

    public ReductionPanelCompanion(ListEditionPanel<ModelSimplifierConfig, ModelSimplifierConfigList> editPanel) {
        this.editPanel = editPanel;
        panel = new SimplifierConfigConfigurePanel();
        editPanel.addPanel(panel, EDIT);

        editPanel.addPanel(new JLabel("select or create a reduction"), EMPTY);
    }

    @Override
    public void setParentList(ModelSimplifierConfigList reductions) {
        this.ctlist = new SimplifierConfigContentList(reductions.getNodeOrder());
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


class SimplifierConfigConfigurePanel extends ListPanel<NodeInfo, SimplifierConfigContentList> implements ChangeListener {

	private final JCheckBox checkbox;
    private ModelSimplifierConfig config;

	SimplifierConfigConfigurePanel() {
        super(ReductionConfigHelper.HELPER, "Selected");

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.checkbox = new JCheckBox("Strict (do not try to remove self-regulated nodes)");
		add(this.checkbox, c);
		this.checkbox.addChangeListener(this);
	}

    public void setConfig(ModelSimplifierConfig config) {
        this.config = config;
        if (list != null) {
            list.setConfig(config);
        }
        checkbox.setSelected(config != null && config.strict);
    }

    @Override
	public void stateChanged(ChangeEvent e) {
        if (config != null) {
            config.strict = checkbox.isSelected();
        }
	}
}

class ReductionConfigHelper extends ListPanelHelper<NodeInfo, SimplifierConfigContentList> {

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
    public Object getValue(SimplifierConfigContentList list, NodeInfo node, int column) {
        if (column == 0) {
            return node;
        }

        if (column == 1) {
            return list.isSelected(node);
        }

        return null;
    }

    public boolean setValue(SimplifierConfigContentList list, int row, int column, Object value) {
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
