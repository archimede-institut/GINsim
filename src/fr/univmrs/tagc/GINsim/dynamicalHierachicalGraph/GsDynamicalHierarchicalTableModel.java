package fr.univmrs.tagc.GINsim.dynamicalHierachicalGraph;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import fr.univmrs.tagc.GINsim.graph.GsGraph;

public class GsDynamicalHierarchicalTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 2922634659695976653L;
	
	private List content = null;
	int rowCount = 0;
	int lastcolCount = 0;
	int colCount = 0;
	int maxrow = 10;

	private GsGraph g;

	public GsDynamicalHierarchicalTableModel(GsGraph g) {
		super();
		this.g = g;
	}
	
	
	/**
	 * set the content of the table model
	 *  
	 * @param content the List containing values
	 */
	public void setContent (List content) {
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
		
		return String.valueOf(((String)content.get(rowIndex)).charAt(columnIndex)); // FIXME : rowIndex*maxrow ?
	}

	/**
	 * @param column a column number
	 * 
	 * @return the name of this column
	 */	
	public String getColumnName(int column) {
			return g.getNodeOrder().get(column).toString();
	}

}
