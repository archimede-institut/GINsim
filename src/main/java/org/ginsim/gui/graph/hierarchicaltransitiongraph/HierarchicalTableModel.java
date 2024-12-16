package org.ginsim.gui.graph.hierarchicaltransitiongraph;

import java.util.List;
import javax.swing.table.AbstractTableModel;

import org.ginsim.core.graph.hierarchicaltransitiongraph.HierarchicalNode;
import org.ginsim.core.graph.hierarchicaltransitiongraph.HierarchicalTransitionGraph;


public class HierarchicalTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 2922634659695976653L;

	private List<byte[]> content = null;

	private List<byte[]> contentNode = null;

	private byte[] childsCount = null;
	private byte[][] extraContent = null;
	private int colCount;
	private String[] extraNames = null;
	private int len;
	private boolean showExtra = true;

	private HierarchicalTransitionGraph htg;

	public HierarchicalTableModel( HierarchicalTransitionGraph g) {

		super();
		this.htg = g;
		len = colCount = g.getNodeOrderSize();
		if (showExtra) {
			extraNames = g.getExtraNames();
			if (extraNames != null && extraNames.length > 0) {
				len += extraNames.length;
			}
		}
	}

	@Override
	public int getRowCount() {
		if (content == null) {
			return 0;
		}
		return content.size();
	}

	@Override
	public int getColumnCount() {
		return len;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (rowIndex >= getRowCount() || columnIndex >= len) {
			return null;
		}

		int i = -10;
		if (columnIndex >= colCount) {
			i = extraContent[rowIndex][columnIndex - colCount];
			if (i < 0 ) {
				return "~*";
				//return a;
			}
			return "~" + String.valueOf(i);
		}

		i = content.get(rowIndex)[columnIndex];
		if (i == -1 ) {
			if (childsCount[columnIndex] < 3){
				int minColl = contentNode.size();
				int maxCol = 0;
				for (int j=0 ; j < contentNode.size(); j++){
					if (contentNode.get(j)[columnIndex] != -1 && contentNode.get(j)[columnIndex] <= minColl){
						minColl = contentNode.get(j)[columnIndex];
					}
					if (contentNode.get(j)[columnIndex] != -1 && contentNode.get(j)[columnIndex] >= maxCol){ maxCol = contentNode.get(j)[columnIndex];}
				}
				return "" + String.valueOf(minColl) + "-" + String.valueOf(maxCol);
			}
			return "*";
		    }
		return String.valueOf(i);
	}

	@Override
	public String getColumnName(int column) {
		if (column >= colCount) {
			return "~"+extraNames[column-colCount];
		}

		return htg.getNodeOrder().get(column).getNodeID();
	}

	public void setContent(HierarchicalNode hnode) {
		this.content = hnode.statesToList();//hnode.statesSet.getChildsCount();
		this.contentNode = hnode.statesSet.statesToFullList();
		this.childsCount = hnode.statesSet.getChildsCount();
		if (extraNames != null && extraNames.length > 0) {
			// fill the extra content
			extraContent = fillExtra();
		}
		fireTableDataChanged();
	}

	private byte[][] fillExtra() {
		if (content == null || content.size() < 1) {
			return null;
		}

		byte[][] extraStates = new byte[content.size()][extraNames.length];
		int i=0;
		for (byte[] state: content) {
			extraStates[i] = htg.fillExtraValues(state, extraStates[i]);
			i++;
		}
		return extraStates;
	}

}
