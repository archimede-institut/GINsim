package org.ginsim.gui.service.tool.dynamicalhierarchicalsimplifier;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.ginsim.graph.common.Graph;
import org.ginsim.graph.dynamicalhierarchicalgraph.DynamicalHierarchicalGraph;
import org.ginsim.graph.dynamicalhierarchicalgraph.DynamicalHierarchicalNode;



public class DynamicalHierarchicalTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 2922634659695976653L;
	
	private List content = null;
	private int colCount;
	
	private DynamicalHierarchicalGraph g;

	public DynamicalHierarchicalTableModel( Graph g) {
		super();
		this.g = (DynamicalHierarchicalGraph) g;
		colCount = g.getNodeOrderSize();
	}
	
	/**
	 * @return the number of row
	 */
	public int getRowCount() {
		if (content == null) return 0;
		return content.size();
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
		if (rowIndex >= getRowCount() || columnIndex >= colCount) {
			return null;
		}
		int i = ((byte[])content.get(rowIndex))[columnIndex];
		if (i == -1 ) return "*";
		return String.valueOf(i);
	}

	/**
	 * @param column a column number
	 * 
	 * @return the name of this column
	 */	
	public String getColumnName(int column) {
		return g.getNodeOrder().get(column).toString();
	}

	public void setContent(DynamicalHierarchicalNode dhnode) {
		this.content = dhnode.statesToList(colCount);
        fireTableStructureChanged();
	}

}
