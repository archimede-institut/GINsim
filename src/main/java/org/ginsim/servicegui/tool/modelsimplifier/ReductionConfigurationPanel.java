package org.ginsim.servicegui.tool.modelsimplifier;

import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.utils.data.GenericList;
import org.ginsim.core.utils.data.GenericListListener;
import org.ginsim.gui.utils.data.GenericListPanel;
import org.ginsim.gui.utils.data.ListEditionPanel;
import org.ginsim.gui.utils.data.ListPanelHelper;
import org.ginsim.service.tool.modelsimplifier.ModelSimplifierConfig;
import org.ginsim.service.tool.modelsimplifier.ModelSimplifierConfigList;
import org.ginsim.service.tool.modelsimplifier.ModelSimplifierConfigManager;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * The panel to configure reduction definitions.
 *
 * @author Aurelien Naldi
 */
public class ReductionConfigurationPanel extends ListEditionPanel<ModelSimplifierConfig> {


    public ReductionConfigurationPanel(RegulatoryGraph graph) {
        this((ModelSimplifierConfigList)ObjectAssociationManager.getInstance().getObject(graph, ModelSimplifierConfigManager.KEY, true));
    }

    public ReductionConfigurationPanel(ModelSimplifierConfigList cfgList) {
        super(new ReductionListHelper(cfgList), cfgList, "modelSimplifier");
        init();
    }

}


class ReductionListHelper extends ListPanelHelper<ModelSimplifierConfig> {

    private static final String EDIT = "edit";
    private static final String EMPTY = "empty";

    private final ModelSimplifierConfigList reductions;
    private final SimplifierConfigContentList ctlist;

    public ReductionListHelper(ModelSimplifierConfigList reductions) {
        this.reductions = reductions;
        if (reductions.size() == 0) {
            create(null);
        }

        canOrder = true;
        canAdd = true;
        canRemove = true;

        ctlist = new SimplifierConfigContentList(reductions.getNodeOrder());


    }

    @Override
    public void selectionUpdated(int[] selection) {
        if (selection == null || selection.length < 1) {
            editPanel.showPanel(EMPTY);
        } else {
            ctlist.mcolHelper = listPanel.getSelectedItem();
            ctlist.refresh();
            editPanel.showPanel(EDIT);
        }
    }

    @Override
    public void fillEditPanel() {
        SimplifierConfigConfigurePanel panel = new SimplifierConfigConfigurePanel();
        panel.setList(ctlist);
        editPanel.addPanel(panel, EDIT);

        editPanel.addPanel(new JLabel("select or create a reduction"), EMPTY);
    }

    @Override
    public int doCreate(Object arg) {
        return reductions.create();
    }

    @Override
    public boolean doRemove(int[] sel) {
        if (sel == null || sel.length < 1) {
            return false;
        }

        for (int i=sel.length-1 ; i>-1 ; i--) {
            reductions.remove(i);
        }
        return true;
    }
}


class SimplifierConfigConfigurePanel extends GenericListPanel<RegulatoryNode>
	implements GenericListListener, ChangeListener {
	JCheckBox checkbox;

	SimplifierConfigConfigurePanel() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		this.checkbox = new JCheckBox("Strict (do not try to remove self-regulated nodes)");
		add(this.checkbox, c);
		this.checkbox.addChangeListener(this);
	}

	@Override
    public void setList(GenericList<RegulatoryNode> list) {
    	super.setList(list);
    	list.addListListener(this);
    }

	public void contentChanged() {
    	if (list.mcolHelper != null) {
    		checkbox.setSelected(((ModelSimplifierConfig)list.mcolHelper).strict);
    	}
	}
	public void itemAdded(Object item, int pos) {
	}
	public void itemRemoved(Object item, int pos) {
	}
	public void structureChanged() {
	}

	public void stateChanged(ChangeEvent e) {
    	if (list.mcolHelper != null) {
    		((ModelSimplifierConfig)list.mcolHelper).strict = checkbox.isSelected();
    	}
	}
}
