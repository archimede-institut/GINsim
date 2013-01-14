package org.ginsim.servicegui.export.cadp;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.gui.utils.widgets.EnhancedJTable;
import org.ginsim.servicegui.tool.composition.CompositionSpecificationDialog;

/**
 * 
 * Model for the specification of the initial states of the LRMs being composed
 * 
 */
public class LRMInitialStateModel extends AbstractTableModel {

	private static final long serialVersionUID = -1133482826268556900L;
	private List<RegulatoryNode> nodeOrder;
	private List<byte[]> initialStates = null;
	private CompositionSpecificationDialog dialog;
	private int nbCol;
	private int nbRow;
	private InitialStatesWidget panel;
	private JTable theTable;

	/**
	 * Constructor
	 * 
	 * @param panel
	 * @param dialog
	 */
	public LRMInitialStateModel(InitialStatesWidget panel,
			CompositionSpecificationDialog dialog) {
		super();
		this.panel = panel;
		this.dialog = dialog;
		this.nodeOrder = this.dialog.getGraph().getNodeOrder();
		this.nbCol = this.nodeOrder.size();
		this.nbRow = this.dialog.getNumberInstances();
		this.initialStates = new ArrayList<byte[]>();
		for (int i = 0; i < this.nbRow; i++) {
			byte state[] = new byte[this.nbCol];
			this.initialStates.add(state);
		}

	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {

		if (rowIndex < 0 || columnIndex < 0)
			return false;

		// TODO: Consult with Composition Specification Config to determine
		// whether
		// a particular input component is mapped (it could be a mapped input
		// but have no neighbours)
		if (this.dialog.getMappedNodes().contains(
				this.nodeOrder.get(columnIndex)))
			return false;
		else
			return true;
	}

	/**
	 * 
	 * @param element
	 * @param maxvalue
	 * @return a formated String showing values for this component
	 */
	public static String showValue(Integer element, int maxValue) {
		String out = "";

		if (element == null)
			return out;

		if (element.intValue() == maxValue)
			out += "M" + maxValue;
		else
			out += element.intValue();

		return out;
	}

	/**
	 * 

	 */
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		panel.setMessage("");

		if (rowIndex > this.nbRow || columnIndex > this.nbCol) {
			return;
		}

		if (rowIndex < 0 || columnIndex < 0)
			return;

		int[] r_sel = theTable.getSelectedRows();
		int[] c_sel = theTable.getSelectedColumns();
		for (int i = 0; i < r_sel.length; i++) {
			for (int j = 0; j < c_sel.length; j++) {
				doSetValueAt(value, r_sel[i], c_sel[j]);
			}
		}
		fireTableDataChanged();
	}

	public void doSetValueAt(Object value, int rowIndex, int columnIndex) {
		if (columnIndex < 0)
			return;

		// TODO: implement automatic computation of value for mapped input
		// components
		// Determine whether update is an argument for a mapped input component
		// Make impact automatic

		int maxvalue = this.nodeOrder.get(columnIndex).getMaxValue();

		String stringValue = (String) value;
		if (stringValue.startsWith("M"))
			stringValue = stringValue.substring(1);
		else {
			try {
				stringValue = "" + Integer.parseInt(stringValue);
			} catch (Exception e) {
				stringValue = "0";
			}
		}

		Integer intValue = Integer.parseInt((String) stringValue);
		if (intValue > maxvalue) {
			this.panel.setMessage("Indicated value (" + intValue
					+ ") exceeds maximum value allowed for the component");
		} else {
			this.initialStates.get(rowIndex)[columnIndex] = (byte) intValue
					.intValue();
		}

		fireTableCellUpdated(rowIndex, columnIndex);

	}

	public void setTable(EnhancedJTable tableInitStates) {
		theTable = tableInitStates;
	}

	@Override
	public int getColumnCount() {
		return this.nbCol;
	}

	@Override
	public int getRowCount() {
		return this.nbRow;
	}

	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	public String getColumnName(int columnIndex) {
		if (columnIndex < 0 || columnIndex >= getColumnCount())
			return "";
		return this.nodeOrder.get(columnIndex).getNodeInfo()
				.getNodeID();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex >= getRowCount() || columnIndex >= getColumnCount())
			return "";

		Integer value = new Integer(
				(int) this.initialStates.get(rowIndex)[columnIndex]);
		int maxvalue = this.nodeOrder.get(columnIndex).getMaxValue();
		return showValue(value, maxvalue);
	}

	public List<byte[]> getInitialStates() {
		return this.initialStates;
	}
}
