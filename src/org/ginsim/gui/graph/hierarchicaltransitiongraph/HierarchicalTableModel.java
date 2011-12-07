package org.ginsim.gui.graph.hierarchicaltransitiongraph;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.hierachicaltransitiongraph.HierarchicalNode;
import org.ginsim.core.graph.hierachicaltransitiongraph.HierarchicalTransitionGraph;



public class HierarchicalTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 2922634659695976653L;
		
		private List<byte[]> content = null;
		private int colCount;
		
		private HierarchicalTransitionGraph htg;

		public HierarchicalTableModel( Graph g) {
			
			super();
			this.htg = (HierarchicalTransitionGraph) g;
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
			
			return htg.getNodeOrder().get(column).getNodeID();
		}

		public void setContent(HierarchicalNode hnode) {
			this.content = hnode.statesToList();
	        fireTableStructureChanged();
		}

	}
