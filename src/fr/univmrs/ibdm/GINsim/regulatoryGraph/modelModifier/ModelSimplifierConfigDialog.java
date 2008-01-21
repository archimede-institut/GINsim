package fr.univmrs.ibdm.GINsim.regulatoryGraph.modelModifier;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.datastore.gui.GenericListPanel;
import fr.univmrs.tagc.widgets.EnhancedJTable;
import fr.univmrs.tagc.widgets.StackDialog;


public class ModelSimplifierConfigDialog extends StackDialog {
	private static final long	serialVersionUID	= 3618855894072951620L;

	GsRegulatoryGraph graph;
	GenericListPanel lp;
	
	ModelSimplifierConfigDialog(GsRegulatoryGraph graph) {
		super(graph.getGraphManager().getMainFrame(), "modelSimplifier", 600, 500);
		this.graph = graph;
		setTitle("select nodes to remove");
		
		
        ModelSimplifierConfigList cfgList = (ModelSimplifierConfigList)graph.getObject(ModelSimplifierConfigManager.key, true);
		if (cfgList.getNbElements(null) == 0) {
			cfgList.add();
		}
        SimplifierConfigPanel panel = new SimplifierConfigPanel();
        Map m = new HashMap();
        m.put(ModelSimplifierConfig.class, panel);
        lp = new GenericListPanel(m);
        lp.setList(cfgList);
		panel.setEditedObject(graph.getNodeOrder(), lp);
		setMainPanel(lp);
		setVisible(true);
	}
	
	protected void run() {
		if (lp.getSelectedItem() != null) {
			new ModelSimplifier(graph, (ModelSimplifierConfig)lp.getSelectedItem(), this);
		}
	}
	
    public void endSimu(GsGraph graph) {
        if (null == graph) {
            GsEnv.error("no state transition graph", this.graph.getGraphManager().getMainFrame());
        } else {
            GsEnv.whatToDoWithGraph(this.graph.getGraphManager().getMainFrame(), graph, false);
        }
        cancel();
    }
}

class SimplifierConfigPanel extends JPanel implements ListSelectionListener {
	private static final long	serialVersionUID	= 1112333567261768396L;
	
	private SimplifierConfigTableModel model = new SimplifierConfigTableModel();
	private List nodeOrder;
	GenericListPanel lp;

	public SimplifierConfigPanel() {
		JScrollPane sp = new JScrollPane();
		sp.setViewportView(new EnhancedJTable(model));
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		add(sp, c);
	}
	
	public void setEditedObject(List nodeOrder, GenericListPanel lp) {
		this.nodeOrder = nodeOrder;
		this.lp = lp;
		lp.addSelectionListener(this);
		valueChanged(null);
	}

	public void valueChanged(ListSelectionEvent e) {
		model.setSelected(nodeOrder, (ModelSimplifierConfig)lp.getSelectedItem());
	}
}

class SimplifierConfigTableModel extends AbstractTableModel {
	private static final long	serialVersionUID	= 652081136537658336L;
	
	List nodeOrder;
	ModelSimplifierConfig config;
	
	public void setSelected(List nodes, ModelSimplifierConfig config) {
		this.nodeOrder = nodes;
		this.config = config;
		fireTableDataChanged();
	}
	
	public int getColumnCount() {
		return 2;
	}

	public int getRowCount() {
		if (nodeOrder == null) {
			return 0;
		}
		return nodeOrder.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return nodeOrder.get(rowIndex).toString();
		}
		if (columnIndex == 1) {
			return config.m_removed.containsKey(nodeOrder.get(rowIndex)) ? Boolean.TRUE : Boolean.FALSE;
		}
		return null;
	}

	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		if (columnIndex == 1) {
			if (value == Boolean.TRUE) {
				config.m_removed.put(nodeOrder.get(rowIndex), null);
			} else {
				config.m_removed.remove(nodeOrder.get(rowIndex));
			}
		}
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnIndex == 1;
	}

	public Class getColumnClass(int columnIndex) {
		switch (columnIndex) {
			case 0:
				return String.class;
			case 1:
				return Boolean.class;
			default:
				return super.getColumnClass(columnIndex);
		}
	}
}