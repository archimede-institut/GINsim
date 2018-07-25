package org.ginsim.gui.graph.hierarchicaltransitiongraph;

import java.util.List;
import javax.swing.table.AbstractTableModel;

import org.ginsim.core.graph.hierarchicaltransitiongraph.HierarchicalNode;
import org.ginsim.core.graph.hierarchicaltransitiongraph.HierarchicalTransitionGraph;


public class HierarchicalTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 2922634659695976653L;

	private List<byte[]> content = null;
	private byte[][] extraContent = null;
	private int colCount;
	private String[] extraNames;
	private int len;

	private HierarchicalTransitionGraph htg;

	public HierarchicalTableModel( HierarchicalTransitionGraph g) {

		super();
		this.htg = g;
		len = colCount = g.getNodeOrderSize();
		extraNames = g.getExtraNames();
		if (extraNames != null && extraNames.length > 0) {
			len += extraNames.length;
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
		} else {
			i = content.get(rowIndex)[columnIndex];
		}
		if (i == -1 ) {
			return "*";
		}
		return String.valueOf(i);
	}

	@Override
	public String getColumnName(int column) {
		if (column >= colCount) {
			return extraNames[column-colCount];
		}

		return htg.getNodeOrder().get(column).getNodeID();
	}

	public void setContent(HierarchicalNode hnode) {
		this.content = hnode.statesToList();
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
