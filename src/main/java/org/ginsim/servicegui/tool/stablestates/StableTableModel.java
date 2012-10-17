package org.ginsim.servicegui.tool.stablestates;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.PathSearcher;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.initialstate.GsInitialStateList;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialStateList;
import org.ginsim.core.graph.regulatorygraph.initialstate.InitialStateManager;

/**
 * Simple table model to view stable state search results.
 */
@SuppressWarnings("serial")
public class StableTableModel extends AbstractTableModel {

	int nbcol = 0;
	List<byte[]> result = new ArrayList<byte[]>();
	MDDManager factory;
	Object[] variables;
	InitialStateList istates = null;

	public StableTableModel() {
	}
	public StableTableModel(RegulatoryGraph lrg) {
		GsInitialStateList gsistates = (GsInitialStateList) ObjectAssociationManager.getInstance().getObject(lrg, InitialStateManager.KEY, false);
		if (gsistates != null) {
			istates = gsistates.getInitialStates();
		}
	}

	public byte[] getState(int sel) {
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
				byte[] r = new byte[path.length];
				for (int i=0 ; i<path.length ; i++) {
					r[i] = (byte)path[i];
				}
				result.add(r);
			}
		}
		
		fireTableStructureChanged();
	}
	
	public void setResult(List<byte[]> stables, List<?> variables) {
		result.clear();
		this.factory = null;
		for (byte[] path: stables) {
			byte[] r = new byte[path.length];
			for (int i=0 ; i<path.length ; i++) {
				r[i] = path[i];
			}
			result.add(r);
		}
		
		this.variables = variables.toArray();
		this.nbcol = this.variables.length;
		fireTableStructureChanged();
	}
	
	@Override
	public int getRowCount() {
		return result.size();
	}

	@Override
	public int getColumnCount() {
		if (nbcol > 0) {
			return nbcol+1;
		}
		return 0;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			if (istates != null && variables != null) {
				return istates.nameStateInfo(result.get(rowIndex), variables);
			}
			return "";
		}
		int v = result.get(rowIndex)[columnIndex-1];
		if (v == 0) {
			return "0";
		}
		if (v < 0) {
			return "*";
		}
		return ""+v;
	}

	@Override
	public String getColumnName(int column) {
		if (column == 0) {
			return "Name";
		}
		
		if (variables != null) {
			return variables[column-1].toString();
		}
		return super.getColumnName(column-1);
	}
}
