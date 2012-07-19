package org.ginsim.servicegui.tool.stablestates;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.MDDVariable;
import org.colomoto.mddlib.PathSearcher;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;

/**
 * Simple table model to view stable state search results.
 */
@SuppressWarnings("serial")
public class StableTableModel extends AbstractTableModel {

	int nbcol = 0;
	List<int[]> result = new ArrayList<int[]>();
	MDDManager factory;
	Object[] variables;

	public int[] getState(int sel) {
		return result.get(sel);
	}

	public void setResult(MDDManager factory, int idx) {
		result.clear();
		this.factory = factory;
		this.variables = factory.getAllVariables();
		nbcol = variables.length;
		if (!factory.isleaf(idx)) {
			PathSearcher searcher = new PathSearcher(factory, 1);
			int[] path = searcher.setNode(idx);
			for (int l: searcher) {
				result.add(path.clone());
			}
		}
		
		fireTableStructureChanged();
	}
	
	public void setResult(List<byte[]> stables, List<?> variables) {
		result.clear();
		this.factory = null;
		for (byte[] path: stables) {
			int[] r = new int[path.length];
			for (int i=0 ; i<path.length ; i++) {
				r[i] = path[i];
			}
			result.add(r);
		}
		
		this.variables = variables.toArray();
		
		fireTableStructureChanged();
	}
	
	@Override
	public int getRowCount() {
		return result.size();
	}

	@Override
	public int getColumnCount() {
		return nbcol;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		int v = result.get(rowIndex)[columnIndex];
		if (v == 0) {
			return "";
		}
		if (v < 0) {
			return "*";
		}
		return ""+v;
	}

	@Override
	public String getColumnName(int column) {
		if (factory != null) {
			return variables[column].toString();
		}
		return super.getColumnName(column);
	}
}
