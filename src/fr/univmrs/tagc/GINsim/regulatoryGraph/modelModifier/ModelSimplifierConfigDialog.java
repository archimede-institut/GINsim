package fr.univmrs.tagc.GINsim.regulatoryGraph.modelModifier;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.ginsim.graph.Graph;
import org.ginsim.gui.GUIManager;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.common.Tools;
import fr.univmrs.tagc.common.datastore.GenericList;
import fr.univmrs.tagc.common.datastore.GenericListListener;
import fr.univmrs.tagc.common.datastore.SimpleGenericList;
import fr.univmrs.tagc.common.datastore.gui.GenericListPanel;
import fr.univmrs.tagc.common.widgets.StackDialog;

public class ModelSimplifierConfigDialog extends StackDialog implements ListSelectionListener {
	private static final long	serialVersionUID	= 3618855894072951620L;

	GsRegulatoryGraph graph;
	GenericListPanel<ModelSimplifierConfig> lp;
	SimplifierConfigContentList ctlist;
	boolean isRunning = false;
	
	ModelSimplifierConfigDialog(GsRegulatoryGraph graph) {
		super(graph, "modelSimplifier", 600, 500);
		this.graph = graph;
		setTitle("select nodes to remove");
		
        ModelSimplifierConfigList cfgList = (ModelSimplifierConfigList)graph.getObject(ModelSimplifierConfigManager.key, true);
		if (cfgList.getNbElements(null) == 0) {
			cfgList.add();
		}
		ctlist = new SimplifierConfigContentList(graph.getNodeOrder());
		SimplifierConfigConfigurePanel panel = new SimplifierConfigConfigurePanel();
        panel.setList(ctlist);
        Map<Class<?>, Component> m = new HashMap<Class<?>, Component>();
        m.put(ModelSimplifierConfig.class, panel);
        lp = new GenericListPanel<ModelSimplifierConfig>(m, "modelSimplifier");
        lp.addSelectionListener(this);
        lp.setList(cfgList);
		setMainPanel(lp);
		setVisible(true);
	}
	
	protected void run() {
		if (!isRunning && lp.getSelectedItem() != null) {
			isRunning = true;
			new ModelSimplifier(graph, (ModelSimplifierConfig)lp.getSelectedItem(), this, true);
	        brun.setEnabled(false);
		}
	}
	
    public void endSimu( Graph graph, Exception e) {
    	isRunning = false;
        if (null == graph) {
            Tools.error(e.getMessage(), this.graph);
            brun.setEnabled(true);
        } else {
            GUIManager.getInstance().whatToDoWithGraph(this.graph, graph, false);
            cancel();
        }
    }

	public void valueChanged(ListSelectionEvent e) {
		ctlist.mcolHelper = (ModelSimplifierConfig)lp.getSelectedItem();
		ctlist.refresh();
	}

	public boolean showPartialReduction(List<RemovedInfo> l_todo) {

        int choice = JOptionPane.showConfirmDialog(this, "show result of partial reduction?", "Reduction failed",
				JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);

		if (choice == JOptionPane.NO_OPTION) {
	    	isRunning = false;
			cancel();
			return false;
		}
		return true;
	}
}


class SimplifierConfigContentList extends SimpleGenericList<GsRegulatoryVertex> {

	SimplifierConfigContentList(List<GsRegulatoryVertex> nodeOrder) {
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

class SimplifierConfigConfigurePanel extends GenericListPanel<GsRegulatoryVertex> 
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
    public void setList(GenericList<GsRegulatoryVertex> list) {
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