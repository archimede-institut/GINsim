package fr.univmrs.ibdm.GINsim.regulatoryGraph.modelModifier;

import java.awt.GridBagLayout;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

import fr.univmrs.ibdm.GINsim.global.GsEnv;
import fr.univmrs.ibdm.GINsim.graph.GsGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.widgets.EnhancedJTable;
import fr.univmrs.tagc.widgets.StackDialog;


public class ModelSimplifierConfigDialog extends StackDialog {
	private static final long	serialVersionUID	= 3618855894072951620L;

	GsRegulatoryGraph graph;
	ModelSimplifierConfig cfg = new ModelSimplifierConfig();
	
	ModelSimplifierConfigDialog(GsRegulatoryGraph graph) {
		super(graph.getGraphManager().getMainFrame(), "modelSimplifier", 600, 500);
		this.graph = graph;
		setTitle("select nodes to remove");
		setMainPanel(new SimplifierConfigPanel(graph.getNodeOrder(), cfg));
		setVisible(true);
	}
	
	protected void run() {
		new ModelSimplifier(graph, cfg, this);
	}
	
    public void endSimu(GsGraph graph) {
        if (null == graph) {
            GsEnv.error("no state transition graph", this.graph.getGraphManager().getMainFrame());
        } else {
            GsEnv.whatToDoWithGraph(this.graph.getGraphManager().getMainFrame(), graph);
        }
        cancel();
    }
}

class SimplifierConfigPanel extends JPanel {
	private static final long	serialVersionUID	= 1112333567261768396L;
	
	private SimplifierConfigTableModel model = new SimplifierConfigTableModel();

	public SimplifierConfigPanel(Vector nodeOrder, ModelSimplifierConfig cfg) {
		model.setSelected(nodeOrder, cfg);
		JScrollPane sp = new JScrollPane();
		sp.setViewportView(new EnhancedJTable(model));
		setLayout(new GridBagLayout());
		add(sp);
	}
}

class SimplifierConfigTableModel extends AbstractTableModel {
	private static final long	serialVersionUID	= 652081136537658336L;
	
	Vector nodeOrder;
	ModelSimplifierConfig config;
	
	public void setSelected(Vector nodes, ModelSimplifierConfig config) {
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