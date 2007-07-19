package fr.univmrs.ibdm.GINsim.stableStates;

import java.awt.Checkbox;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.OmddNode;

public class StableTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 3483674324331745743L;
	
	Vector nodeOrder;
	Vector v_stable;
	
	boolean checkbox;
	Vector checklist;
	
	public StableTableModel(Vector nodeOrder) {
		this(nodeOrder, false);
	}
	
	public StableTableModel(Vector nodeOrder, boolean checkbox) {
		this.nodeOrder = nodeOrder;
		this.checkbox = checkbox;
		v_stable = new Vector();
	}
	
	public int getColumnCount() {
		return checkbox ? nodeOrder.size()+1 : nodeOrder.size();
	}

	public int getRowCount() {
		return v_stable.size();
	}

	public Class getColumnClass(int columnIndex) {
		if (checkbox && columnIndex == 0) {
			return Boolean.class;
		}
		return super.getColumnClass(columnIndex);
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (checkbox && columnIndex == 0) {
			return true; // TODO: real check value
		}
		return false;
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (checkbox && columnIndex == 0) {
			// TODO: edit checkbox
		}
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		int[] t_state = (int[])v_stable.get(rowIndex);
		int val;
		if (checkbox) {
			if (columnIndex == 0) {
				return Boolean.FALSE;
			}
			val = t_state[columnIndex-1];
		} else {
			val = t_state[columnIndex];
		}
		if (val == -1) {
			return "*";
		}
		return ""+val;
	}

	public void setResult(OmddNode stable) {
		v_stable.clear();
		
		int[] state = new int[nodeOrder.size()];
		for (int i=0 ; i<state.length ; i++) {
			state[i] = -1;
		}
		findStableState(state, stable);
		fireTableDataChanged();
		//	TODO: create/reset checklist
		checklist = new Vector();
		checklist.clear();
	}
	
	public void setResult(Vector v_stable) {
		this.v_stable = v_stable;
		fireTableDataChanged();
	}
	
	public String getColumnName(int column) {
		if (checkbox) {
			if (column == 0) {
				return "check";
			}
			return nodeOrder.get(column-1).toString();
		}
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