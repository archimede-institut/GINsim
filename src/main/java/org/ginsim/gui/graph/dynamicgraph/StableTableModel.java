package org.ginsim.gui.graph.dynamicgraph;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.colomoto.biolqm.NodeInfo;
import org.colomoto.biolqm.tool.fixpoints.FixpointList;
import org.colomoto.mddlib.MDDManager;
import org.colomoto.mddlib.PathSearcher;
import org.ginsim.core.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStateList;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStatesHandler;
import org.ginsim.core.graph.regulatorygraph.namedstates.NamedStatesManager;

/**
 * Simple table model to view stable state search results.
 */
@SuppressWarnings("serial")
public class StableTableModel extends AbstractTableModel {

	List<byte[]> result = null;
	List<?> components = null;
	NamedStateList istates = null;

	public StableTableModel() {
	}
	public StableTableModel(RegulatoryGraph lrg) {
		NamedStatesHandler gsistates = (NamedStatesHandler) ObjectAssociationManager.getInstance().getObject(lrg, NamedStatesManager.KEY, false);
		if (gsistates != null) {
			istates = gsistates.getInitialStates();
		}
	}

	public byte[] getState(int sel) {
		if (sel < 0 || result == null | sel > result.size()) {
			return null;
		}
		return result.get(sel);
	}

	public void setResult(FixpointList fixpoints) {
		setResult(fixpoints, fixpoints.nodes);
	}

	public void setResult(List<byte[]> stables, List<?> components) {
		this.result = stables;
		this.components = components;
		fireTableStructureChanged();
	}

	@Override
	public int getRowCount() {
		if (result == null) {
			return 0;
		}

		return result.size();
	}

	@Override
	public int getColumnCount() {
		if (result == null) {
			return 0;
		}

		return components.size();
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (result == null) {
			return "";
		}
		if (columnIndex == 0) {
			if (istates != null) {
				return istates.nameState(result.get(rowIndex), components);
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

		if (components == null) {
			return null;
		}
		
		return components.get(column-1).toString();
	}
}
