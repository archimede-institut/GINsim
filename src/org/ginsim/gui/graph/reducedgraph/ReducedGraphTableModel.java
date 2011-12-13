package org.ginsim.gui.graph.reducedgraph;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

/**
 * this model is used to list the content of a strong components.
 * it's not editable.
 * it will display the content of a vetor in a table, with maxrow (ie 10) elements per line
 */
public class ReducedGraphTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 9012745010038236066L;
	private Vector content = null;
	int rowCount = 0;
	int lastcolCount = 0;
	int colCount = 0;
	int maxrow = 10;

	/**
	 * set the content of the table model
	 *  
	 * @param content the vector containing values
	 */
	public void setContent (Vector content) {
		this.content = content;
		int size = content.size();
		if (size >maxrow) {
			colCount = maxrow;
			rowCount = size / maxrow + 1;
			lastcolCount = size % maxrow;
		} else {
			colCount = lastcolCount = size;
			rowCount = 1;
		}
		fireTableStructureChanged();
	}

	/**
	 * @return the number of row
	 */
	public int getRowCount() {
		return rowCount;
	}

	/**
	 * @return the number of column
	 */
	public int getColumnCount() {
		return colCount;
	}


	/**
	 * @param rowIndex
	 * @param columnIndex
	 * 
	 * @return the value at the specified position
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex >= rowCount || columnIndex >= colCount) {
			return null;
		}
		
		if (rowIndex == rowCount-1 && columnIndex >= lastcolCount) {
			return "-";
		}
		
		return content.get((rowIndex*maxrow) + columnIndex);
	}

	/**
	 * @param column a column number
	 * 
	 * @return the name of this column
	 */	
	public String getColumnName(int column) {
			return "" + column;
	}
}
