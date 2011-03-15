package fr.univmrs.tagc.GINsim.hierachicalTransitionGraph;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import fr.univmrs.tagc.GINsim.graph.GsGraph;

public class GsHierarchicalTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 2922634659695976653L;
		
		private List content = null;
		private int colCount;
		
		private GsHierarchicalTransitionGraph htg;

		public GsHierarchicalTableModel(GsGraph g) {
			super();
			this.htg = (GsHierarchicalTransitionGraph) g;
			colCount = g.getNodeOrder().size()+1;
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
			if (columnIndex == 0) return (rowIndex+1)+":";
			int i = ((byte[])content.get(rowIndex))[columnIndex-1];
			if (i == -1 ) return "*";
			return String.valueOf(i);
		}

		/**
		 * @param column a column number
		 * 
		 * @return the name of this column
		 */	
		public String getColumnName(int column) {
			if (column == 0) return "#";
			return htg.getNodeOrder().get(column-1).toString();
		}

		public void setContent(GsHierarchicalNode dhnode) {
			this.content = dhnode.statesToList();
	        fireTableStructureChanged();
		}

	}
