package org.ginsim.servicegui.tool.modelsimplifier;

import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.utils.data.GenericList;
import org.ginsim.core.utils.data.GenericListListener;
import org.ginsim.gui.utils.data.GenericListPanel;
import org.ginsim.service.tool.modelsimplifier.ModelSimplifierConfig;
import org.ginsim.service.tool.modelsimplifier.ModelSimplifierConfigList;
import org.ginsim.service.tool.modelsimplifier.ModelSimplifierConfigManager;

import java.awt.*;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;


/**
 * The panel to configure reduction definitions.
 *
 * @author Aurelien Naldi
 */
public class ReductionConfigurationPanel extends GenericListPanel<ModelSimplifierConfig> {


    public static ReductionConfigurationPanel getPanel(RegulatoryGraph graph) {
        return getPanel((ModelSimplifierConfigList) ObjectAssociationManager.getInstance().getObject(graph, ModelSimplifierConfigManager.KEY, true));
    }

    public static ReductionConfigurationPanel getPanel(ModelSimplifierConfigList cfgList) {
        Map<Class<?>, Component> m = new HashMap<Class<?>, Component>();
        SimplifierConfigContentList ctlist = new SimplifierConfigContentList(cfgList.getNodeOrder());

        SimplifierConfigConfigurePanel panel = new SimplifierConfigConfigurePanel();
        panel.setList(ctlist);
        m.put(ModelSimplifierConfig.class, panel);

        ReductionConfigurationPanel cfgPanel = new ReductionConfigurationPanel(m, cfgList, ctlist);
        return cfgPanel;
    }

    private final SimplifierConfigContentList ctlist;

    private ReductionConfigurationPanel(Map<Class<?>, Component> m, ModelSimplifierConfigList cfgList, SimplifierConfigContentList ctlist) {
        super(m, "modelSimplifier");
        if (cfgList.getNbElements(null) == 0) {
            cfgList.add();
        }

        this.ctlist = ctlist;

        this.setList(cfgList);

    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        super.valueChanged(e);
        ctlist.mcolHelper = (ModelSimplifierConfig)getSelectedItem();
        ctlist.refresh();
    }

}


class SimplifierConfigConfigurePanel extends GenericListPanel<RegulatoryNode>
	implements GenericListListener, ChangeListener {
	private static final long serialVersionUID = -2219030309910143737L;
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
