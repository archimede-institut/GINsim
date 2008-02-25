package fr.univmrs.tagc.GINsim.stableStates;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import fr.univmrs.tagc.GINsim.regulatoryGraph.OmddNode;

public class StableTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 3483674324331745743L;
	
	List nodeOrder;
	List v_stable;
	
	boolean checkbox;
	List checklist;
	
	int checkIndex = -1;
	
	public StableTableModel(List nodeOrder) {
		this(nodeOrder, false);
	}
	
	public StableTableModel(List nodeOrder, boolean checkbox) {
		this.nodeOrder = nodeOrder;
		this.checkbox = checkbox;
		v_stable = new ArrayList();
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
			return true;
		}
		return false;
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (checkbox && columnIndex == 0) {
			if (aValue == Boolean.TRUE) {
				checkIndex = rowIndex;
			} else if (rowIndex == checkIndex) {
				checkIndex = -1;
			}
		}
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		int[] t_state = (int[])v_stable.get(rowIndex);
		int val;
		if (checkbox) {
			if (columnIndex == 0) {
				return rowIndex == checkIndex ? Boolean.TRUE : Boolean.FALSE;
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
		checkIndex = -1;
		checklist = new ArrayList();
		checklist.clear();
	}
	
	public void setResult(List v_stable) {
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
	
	public int[] getCheckedRow() {
		if (checkIndex == -1 || checkIndex >= getRowCount()) {
			return null;
		}
		return (int[])v_stable.get(checkIndex);
	}
}