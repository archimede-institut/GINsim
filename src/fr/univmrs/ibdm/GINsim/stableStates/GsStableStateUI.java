package fr.univmrs.ibdm.GINsim.stableStates;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.AbstractTableModel;

import fr.univmrs.ibdm.GINsim.gui.GsJTable;
import fr.univmrs.ibdm.GINsim.gui.GsStackDialog;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.OmddNode;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.mutant.MutantSelectionPanel;

public class GsStableStateUI extends GsStackDialog {
	private static final long serialVersionUID = -3605525202652679586L;
	
	GsRegulatoryGraph graph;
	stableTableModel tableModel;
	MutantSelectionPanel mutantPanel;
	JPanel buttonPanel;
	
	public GsStableStateUI(GsRegulatoryGraph graph) {
		super(graph.getGraphManager().getMainFrame(), "display.stableStates", 200, 100);
		this.graph = graph;
		Container panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		mutantPanel = new MutantSelectionPanel(this, graph);
		panel.add(mutantPanel, c);

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		JScrollPane sp = new JScrollPane();
		tableModel = new stableTableModel(graph.getNodeOrder());

        GsJTable tableResult = new GsJTable(tableModel);
        tableResult.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        tableResult.getTableHeader().setReorderingAllowed(false);
		sp.setViewportView(tableResult);
		panel.add(sp, c);
		
		setMainPanel(panel);
	}
	
	protected void run() {
		setRunning(true);
		new GsSearchStableStates(graph, mutantPanel.getMutant(), this).start();
	}
	
	protected void setResult(OmddNode stable) {
		tableModel.setResult(stable);
		setRunning(false);
	}
	
	public void doClose() {
		setVisible(false);
		dispose();
	}
}

class stableTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 3483674324331745743L;
	
	Vector nodeOrder;
	Vector v_stable;
	
	public stableTableModel(Vector nodeOrder) {
		this.nodeOrder = nodeOrder;
		v_stable = new Vector();
	}
	
	public int getColumnCount() {
		return nodeOrder.size();
	}

	public int getRowCount() {
		return v_stable.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		int[] t_state = (int[])v_stable.get(rowIndex);
		if (t_state[columnIndex] == -1) {
			return "*";
		}
		return ""+t_state[columnIndex];
	}

	public void setResult(OmddNode stable) {
		v_stable.clear();
		int[] state = new int[nodeOrder.size()];
		for (int i=0 ; i<state.length ; i++) {
			state[i] = -1;
		}
		findStableState(state, stable);
		fireTableDataChanged();
	}
	
	public String getColumnName(int column) {
		return nodeOrder.get(column).toString();
	}

	private void findStableState(int[] state, OmddNode stable) {
		if (stable.next == null) {
			if (stable.value == 1) {
				v_stable.add(state.clone());
			}
			return;
		}
		for (int i=0 ; i<stable.next.length ; i++) {
			state[stable.level] = i;
			findStableState(state, stable.next[i]);
		}
		state[stable.level] = -1;
	}
}