package fr.univmrs.ibdm.GINsim.reg2dyn;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;

/**
 * tableModel to configure gene state blockers
 */
public class GsReg2dynBlockModel extends AbstractTableModel {

	private static final long serialVersionUID = 864660594916225977L;
	private Vector nodeOrder;
	private int[] t_min;
	private int[] t_max;

	
	/**
	 * @param nodeOrder
	 * @param t_min
	 * @param t_max
	 */
	public GsReg2dynBlockModel(Vector nodeOrder, int[] t_min, int[] t_max) {
		this.nodeOrder = nodeOrder;
		this.t_min = t_min;
		this.t_max = t_max;
	}

	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return t_min.length;
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return 3;
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	public String getColumnName(int columnIndex) {
		switch (columnIndex) {
		case 0:
			return Translator.getString("STR_node");
		case 1:
			return Translator.getString("STR_min");
		case 2:
			return Translator.getString("STR_max");
		}
		return null;
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	public Class getColumnClass(int columnIndex) {
		if (columnIndex > 0 && columnIndex < 3) {
			return String.class;
		}
		return Object.class;
	}

	/**
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (rowIndex > t_min.length) {
			return false;
		}
		switch (columnIndex) {
			case 1:
			case 2:
				return true;
		}
		return false;
	}

	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex > t_min.length) {
			return null;
		}
		int value = -1;
		switch (columnIndex) {
			case 0:
				return nodeOrder.get(rowIndex);
			case 1:
				value = t_min[rowIndex];
				break;
			case 2:
				value = t_max[rowIndex];
				break;
			default:
				return null;
		}
		if (value == -1) {
			return "";
		}
		return ""+value;
	}

	/**
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (rowIndex >= t_min.length || columnIndex < 1 || columnIndex > 2) {
			return;
		}
		
		if ("".equals(aValue) || "-".equals(aValue)) {
			t_min[rowIndex] = -1;
			t_max[rowIndex] = -1;
			fireTableCellUpdated(rowIndex, 1);
			fireTableCellUpdated(rowIndex, 2);
			return;
		}
		
		int val;
		try {
			val = Integer.parseInt((String)aValue);
		} catch (Exception e) {
			return;
		}
		
		if (val == -1) {
			t_min[rowIndex] = -1;
			t_max[rowIndex] = -1;
			fireTableCellUpdated(rowIndex, 1);
			fireTableCellUpdated(rowIndex, 2);
			return;
		}
		if (val < 0 || val > ((GsRegulatoryVertex)nodeOrder.get(rowIndex)).getMaxValue()) {
			return;
		}
		switch (columnIndex) {
			case 1:
				t_min[rowIndex] = val;
				if (t_max[rowIndex] == -1 || t_max[rowIndex] < val) {
					fireTableCellUpdated(rowIndex, 2);
					t_max[rowIndex] = val;
				}
				break;
			case 2:
				t_max[rowIndex] = val;
				if (t_min[rowIndex] == -1 || t_min[rowIndex] > val) {
					fireTableCellUpdated(rowIndex, 1);
					t_min[rowIndex] = val;
				}
				break;
		}
	}
	/**
     * refresh the state blocking.
     * @param nodeOrder
     * @param minBlock
     * @param maxBlock
	 */
    public void refresh(Vector nodeOrder, int[] minBlock, int[] maxBlock) {
        this.nodeOrder = nodeOrder;
        this.t_min = minBlock;
        this.t_max = maxBlock;
        fireTableStructureChanged();
    }
}
