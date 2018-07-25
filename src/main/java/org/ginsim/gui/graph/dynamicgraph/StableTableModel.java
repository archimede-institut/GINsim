package org.ginsim.gui.graph.dynamicgraph;

import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;

import org.colomoto.biolqm.NodeInfo;
import org.colomoto.biolqm.settings.state.StateList;
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
public class StableTableModel extends AbstractTableModel implements ChangeListener {

	StateList states = null;
	NodeInfo[] components = null;
	NamedStateList istates = null;
	byte[] state = null;
	JCheckBox cb_extra;

	public StableTableModel() {
	}

	public StableTableModel(RegulatoryGraph lrg) {
		NamedStatesHandler gsistates = (NamedStatesHandler) ObjectAssociationManager.getInstance().getObject(lrg, NamedStatesManager.KEY, false);
		if (gsistates != null) {
			istates = gsistates.getInitialStates();
		}
	}

	public void setExtraCheckbox(JCheckBox cb_extra) {
		this.cb_extra = cb_extra;
		if(cb_extra != null) {
			cb_extra.addChangeListener(this);
		}
	}

	public byte[] getState(int sel) {
		if (sel < 0 || states == null | sel > states.size()) {
			return null;
		}
		return states.fillState(null, sel);
	}

	public synchronized void setResult(StateList states) {
		if (states == null) {
			this.components = null;
			this.state = new byte[0];
		} else {
			this.components = states.getComponents();
			this.state = new byte[components.length];
		}
		this.states = states;
		fireTableStructureChanged();
	}

	@Override
	public int getRowCount() {
		if (states == null) {
			return 0;
		}

		return states.size();
	}

	@Override
	public int getColumnCount() {
		if (components == null) {
			return 0;
		}

		return components.length + 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (states == null) {
			return "";
		}
		if (columnIndex == 0) {
			if (istates != null) {
				state = states.fillState(state, rowIndex);
				return istates.nameState(state, components);
			}
			return "";
		}
		if (columnIndex > components.length) {
			System.out.println("   out: "+columnIndex);
		}
		int v = states.get(rowIndex, columnIndex-1);
		if (v == -5) {
			return "?";
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
		
		return components[column-1].getNodeID();
	}

	public void stateChanged(ChangeEvent e) {
		if (states == null) {
			return;
		}
		boolean b = cb_extra.isSelected();
		int l = getColumnCount() - 1;
		states.setExtra(b);
		if (states.getComponents().length != l) {
			setResult(states);
		}
	}

}
