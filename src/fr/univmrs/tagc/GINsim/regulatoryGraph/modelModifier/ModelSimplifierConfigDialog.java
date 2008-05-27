package fr.univmrs.tagc.GINsim.regulatoryGraph.modelModifier;

import java.awt.GridBagConstraints;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import fr.univmrs.tagc.GINsim.global.GsEnv;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.datastore.GenericList;
import fr.univmrs.tagc.common.datastore.GenericListListener;
import fr.univmrs.tagc.common.datastore.SimpleGenericList;
import fr.univmrs.tagc.common.datastore.gui.GenericListPanel;
import fr.univmrs.tagc.common.widgets.StackDialog;

public class ModelSimplifierConfigDialog extends StackDialog implements ListSelectionListener {
	private static final long	serialVersionUID	= 3618855894072951620L;

	GsRegulatoryGraph graph;
	GenericListPanel lp;
	SimplifierConfigContentList ctlist;
	boolean isRunning = false;
	
	ModelSimplifierConfigDialog(GsRegulatoryGraph graph) {
		super(graph.getGraphManager().getMainFrame(), "modelSimplifier", 600, 500);
		this.graph = graph;
		setTitle("select nodes to remove");
		
		
        ModelSimplifierConfigList cfgList = (ModelSimplifierConfigList)graph.getObject(ModelSimplifierConfigManager.key, true);
		if (cfgList.getNbElements(null) == 0) {
			cfgList.add();
		}
		ctlist = new SimplifierConfigContentList(graph.getNodeOrder());
		SimplifierConfigConfigurePanel panel = new SimplifierConfigConfigurePanel();
        panel.setList(ctlist);
        Map m = new HashMap();
        m.put(ModelSimplifierConfig.class, panel);
        lp = new GenericListPanel(m, "modelSimplifier");
        lp.addSelectionListener(this);
        lp.setList(cfgList);
		setMainPanel(lp);
		setVisible(true);
	}
	
	protected void run() {
		if (!isRunning && lp.getSelectedItem() != null) {
			isRunning = true;
			new ModelSimplifier(graph, (ModelSimplifierConfig)lp.getSelectedItem(), this);
	        brun.setEnabled(false);
		}
	}
	
    public void endSimu(GsGraph graph, Exception e) {
    	isRunning = false;
        if (null == graph) {
            Tools.error(e.getMessage(), this.graph.getGraphManager().getMainFrame());
            brun.setEnabled(true);
        } else {
            GsEnv.whatToDoWithGraph(this.graph.getGraphManager().getMainFrame(), graph, false);
            cancel();
        }
    }

	public void valueChanged(ListSelectionEvent e) {
		ctlist.mcolHelper = (ModelSimplifierConfig)lp.getSelectedItem();
		ctlist.refresh();
	}
}

class SimplifierConfigContentList extends SimpleGenericList {

	SimplifierConfigContentList(List nodeOrder) {
		super(nodeOrder);
		canAdd = false;
		canOrder = false;
		canEdit = true;
		nbcol = 2;
		t_type = new Class[2];
		t_type[0] = String.class;
		t_type[1] = Boolean.class;
	}
}

class SimplifierConfigConfigurePanel extends GenericListPanel 
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
	
    public void setList(GenericList list) {
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