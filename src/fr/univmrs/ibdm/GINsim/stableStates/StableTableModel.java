package fr.univmrs.ibdm.GINsim.stableStates;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.OmddNode;

public class StableTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 3483674324331745743L;
	
	Vector nodeOrder;
	Vector v_stable;
	
	public StableTableModel(Vector nodeOrder) {
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
	
	public void setResult(Vector v_stable) {
		this.v_stable = v_stable;
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