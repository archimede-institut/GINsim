package org.ginsim.gui.service.tool.stablestates;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.GraphAssociation;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.objectassociation.ObjectAssociationManager;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.graph.regulatorygraph.initialstate.GsInitialStateList;
import org.ginsim.graph.regulatorygraph.initialstate.InitialState;
import org.ginsim.graph.regulatorygraph.initialstate.InitialStateList;
import org.ginsim.graph.regulatorygraph.initialstate.InitialStateManager;
import org.ginsim.graph.regulatorygraph.omdd.OMDDNode;
import org.ginsim.gui.resource.Translator;


public class StableTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 3483674324331745743L;
	
	List nodeOrder;
	List v_stable;
	String[] matches;
	
	boolean checkbox;
	List checklist;
	
	int checkIndex = -1;
	
	public StableTableModel(List nodeOrder) {
		this(nodeOrder, false);
	}
	
	public StableTableModel(List nodeOrder, boolean checkbox) {
		this.nodeOrder = nodeOrder;
		this.checkbox = checkbox;
		v_stable = new ArrayList();
	}
	
	public int getColumnCount() {
		return checkbox ? nodeOrder.size()+2 : nodeOrder.size()+1;
	}

	public int getRowCount() {
		return v_stable.size();
	}

	public Class getColumnClass(int columnIndex) {
		if (checkbox && columnIndex == 0) {
			return Boolean.class;
		}
		return super.getColumnClass(columnIndex);
	}

	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (checkbox && columnIndex == 0) {
			return true;
		}
		return false;
	}

	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (checkbox && columnIndex == 0) {
			if (aValue == Boolean.TRUE) {
				checkIndex = rowIndex;
			} else if (rowIndex == checkIndex) {
				checkIndex = -1;
			}
		}
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		byte[] t_state = (byte[])v_stable.get(rowIndex);
		int val;
		int column = columnIndex;
		if (checkbox) {
			if (column == 0) {
				return rowIndex == checkIndex ? Boolean.TRUE : Boolean.FALSE;
			}
			column--;
		}
		if (column == 0) {
			return matches == null ? "" : matches[rowIndex];
		}
		column--;
		val = t_state[column];
		if (val == -1) {
			return "*";
		}
		return ""+val;
	}

	public void setResult(OMDDNode stable, Graph graph) {
		v_stable.clear();
		
		byte[] state = new byte[nodeOrder.size()];
		for (int i=0 ; i<state.length ; i++) {
			state[i] = -1;
		}
		findStableState(state, stable);
		fireTableDataChanged();
		checkIndex = -1;
		checklist = new ArrayList();
		checklist.clear();
		updateMatches( graph);
	}
	
	public void setResult(List v_stable, Graph graph) throws GsException {
		this.v_stable = v_stable;
		fireTableDataChanged();
		updateMatches( ((GraphAssociation) graph).getAssociatedGraph());
	}
	
	private void updateMatches( Graph graph) {
		InitialStateList initstates = null;
		if (graph != null && graph instanceof RegulatoryGraph) {
		    GsInitialStateList ilist = (GsInitialStateList) ObjectAssociationManager.getInstance().getObject(graph, InitialStateManager.key, false);
		    if (ilist != null) {
		        initstates = ilist.getInitialStates();
		    }
		}
		if (initstates == null) {
			matches = null;
			return;
		}
		int len = v_stable.size();
		matches = new String[len];
		int nbinit = initstates.getNbElements();
		OMDDNode[] t_initMDD = new OMDDNode[nbinit];
		for (int init=0 ; init<nbinit ; init++) {
			t_initMDD[init] = ((InitialState)initstates.getElement(null, init)).getMDD(nodeOrder);
		}
		for (int line=0 ; line<len ; line++) {
			byte[] curstate = (byte[])v_stable.get(line);
			for (int i=0 ; i<nbinit ; i++) {
				OMDDNode node = t_initMDD[i];
				while (node.next != null) {
					byte value = curstate[node.level];
					if (value == -1) {
						OMDDNode next = node.next[0];
						for (int val=0 ; val<node.next.length ; val++) {
							OMDDNode maybenext = node.next[val];
							if (maybenext.next != null || maybenext.value != 0) {
								next = maybenext;
								break;
							}
						}
						node = next;
					} else {
						node = node.next[value];
					}
				}
				if (node.value == 1) {
					String name = ((InitialState)initstates.getElement(null, i)).getName();
					if (matches[line] != null) {
						matches[line] += " ; "+name; 
					} else {
					    matches[line] = name;
					}
				}
			}
		}
	}
	
	public String getColumnName(int columnIndex) {
		int column = columnIndex;
		if (checkbox) {
			if (column == 0) {
				return "check";
			}
			column--;
		}
		if (column == 0) {
			return Translator.getString("STR_name");
		}
		return nodeOrder.get(column-1).toString();
	}

	private void findStableState(byte[] state, OMDDNode stable) {
		if (stable.next == null) {
			if (stable.value == 1) {
				v_stable.add(state.clone());
			}
			return;
		}
		for (byte i=0 ; i<stable.next.length ; i++) {
			state[stable.level] = i;
			findStableState(state, stable.next[i]);
		}
		state[stable.level] = -1;
	}
	
	public byte[] getCheckedRow() {
		if (checkIndex == -1 || checkIndex >= getRowCount()) {
			return null;
		}
		return (byte[])v_stable.get(checkIndex);
	}

	public byte[] getState(int selectedRow) {
		return (byte[])v_stable.get(selectedRow);
	}
}
