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

	
	/**
	 * @param nodeOrder
	 * @param t_min
	 * @param t_max
	 */
	public GsReg2dynBlockModel(Vector nodeOrder) {
		this.nodeOrder = nodeOrder;
	}

	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return nodeOrder.size();
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
		if (rowIndex > getRowCount()) {
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
		if (rowIndex > getRowCount()) {
			return null;
		}
		int value = -1;
		switch (columnIndex) {
			case 0:
				return nodeOrder.get(rowIndex);
			case 1:
				value = ((GsRegulatoryVertex)nodeOrder.get(rowIndex)).getBlockMin();
				break;
			case 2:
				value = ((GsRegulatoryVertex)nodeOrder.get(rowIndex)).getBlockMax();
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
		if (rowIndex >= getRowCount() || columnIndex < 1 || columnIndex > 2) {
			return;
		}
		
		if ("".equals(aValue) || "-".equals(aValue)) {
            ((GsRegulatoryVertex)nodeOrder.get(rowIndex)).setBlockMin((short)-1);
            ((GsRegulatoryVertex)nodeOrder.get(rowIndex)).setBlockMax((short)-1);
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
            ((GsRegulatoryVertex)nodeOrder.get(rowIndex)).setBlockMin((short)-1);
            ((GsRegulatoryVertex)nodeOrder.get(rowIndex)).setBlockMax((short)-1);
			fireTableCellUpdated(rowIndex, 1);
			fireTableCellUpdated(rowIndex, 2);
			return;
		}
		if (val < 0 || val > ((GsRegulatoryVertex)nodeOrder.get(rowIndex)).getMaxValue()) {
			return;
		}
		switch (columnIndex) {
			case 1:
                ((GsRegulatoryVertex)nodeOrder.get(rowIndex)).setBlockMin((short)val);
				break;
			case 2:
                ((GsRegulatoryVertex)nodeOrder.get(rowIndex)).setBlockMax((short)val);
				break;
		}
        fireTableCellUpdated(rowIndex, 1);
        fireTableCellUpdated(rowIndex, 2);
	}
	/**
     * refresh the state blocking.
     * @param nodeOrder
     * @param minBlock
     * @param maxBlock
	 */
    public void refresh(Vector nodeOrder) {
        this.nodeOrder = nodeOrder;
        fireTableStructureChanged();
    }
}
