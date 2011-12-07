package org.ginsim.gui.graph.regulatorygraph.models;

import java.util.List;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.LogicalParameter;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.LogicalParameterList;
import org.ginsim.gui.resource.Translator;


/**
 * This is the model behind the interaction editor
 */
public class TableInteractionsModel extends AbstractTableModel {

	private static final long serialVersionUID = -2093185924206743169L;

	//the vector of interaction
	private LogicalParameterList interactions;

	//the current selected node
	private RegulatoryNode node;
    private RegulatoryGraph graph;

    /**
     * default constructor
     *
     * @param graph
     * @param v_ok
     */
	public TableInteractionsModel(RegulatoryGraph graph, Vector v_ok) {
		super();
		this.interactions = null;
        this.graph = graph;
	}

	/**
	 * constructor with GsNodeRegulationData
	 *
	 * @param no the currently selected node
	 */
	public TableInteractionsModel(RegulatoryNode no) {
		super();
		node = no;
		this.interactions = node.getV_logicalParameters();
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return 2;
	}

	/**
	 * @return size is increased by 1 to have a "inserting" row
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		if (interactions == null) {
			return 0;
		}
		return interactions.size() + 1;
	}

	/**
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		//only the first column is editable, except on the last line
		if ((columnIndex == 0) && (!interactions.isFunction(rowIndex))) {
		    return true;
		}
		return false;
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnClass(int)
	 */
	public Class getColumnClass(int columnIndex) {
		return getValueAt(0, columnIndex).getClass();
	}

	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int rowIndex, int columnIndex) {
		if (interactions == null) {
		    return null;
		}
		if (rowIndex >= interactions.size()) {
		    return ""; // the "inserting" row
		}
		if (columnIndex == 0) {
			return new Integer(((LogicalParameter) interactions.get(rowIndex))
					.getValue());
		}
		return interactions.get(rowIndex).toString();
	}

	/**
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (interactions == null) {
			return;
		}
		if (columnIndex == 0 && rowIndex >= 0 /*&& rowIndex < interactions.getManualSize()*/) {
  		//the first column
			int value = 1;
			if (aValue instanceof Integer) {
				value = ((Integer) aValue).intValue();
			}
			if (aValue instanceof String) {
				try{
					value = Integer.parseInt((String) aValue);
				}
				catch( NumberFormatException nfe){
					value = 0;
				}
			}
			if (value <= node.getMaxValue() && value >= 0) {
				interactions.setParameterValue(rowIndex, value, graph);
			}
			fireTableCellUpdated(rowIndex, columnIndex);
		}
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	public String getColumnName(int columnIndex) {
		if (columnIndex == 0) {
			return Translator.getString("STR_value");
		}
		return Translator.getString("STR_ActiveInteractionEdgeList");
	}

	/**
	 * get all interactions
	 *
	 * @return vector of interactions
	 */
	public LogicalParameterList getInteractions() {
		return interactions;
	}

	/**
	 * remove Interactions at index row
	 *
	 * @param row the row of the interaction, which will be removed
	 */
	public void removeInteractions(int row) {
		if (row < interactions.getManualSize()) {
			interactions.remove(row);
        }
		fireTableRowsDeleted(row, row);
	}

	/**
	 * set the current node
	 *
	 * @param no the current node
	 */
	public void setNode(RegulatoryNode no) {
		node = no;
		if (node != null) {
			this.interactions = node.getV_logicalParameters();
		} else {
			this.interactions = null;
		}
		fireTableDataChanged();
	}

	/**
	 * get the active(s) Edge(s) of the interaction at the row row.
	 *
	 * @param row
	 * @return the active(s) Edge(s) of the interaction at the row row.
	 */
	public List getActivesEdges(int row) {
		if (row > -1 && row < interactions.size()) {
			return ((LogicalParameter) interactions.get(row)).getEdges();
		}
		return null;
	}

	/**
	 * Set the active Edge of an interaction
	 *
	 * @param row
	 * @param edgeIndex
	 */
    public void setActivesEdges(int row, Vector edgeIndex) {
        setActivesEdges(row, edgeIndex, 1);
    }
    /**
     * Set the active Edge of an interaction
     * @param row
     * @param edgeIndex
     * @param value
     */
    public void setActivesEdges(int row, Vector edgeIndex, int value) {
		if (row >= interactions.size()) {
			LogicalParameter inter = new LogicalParameter(value);
			inter.setEdges(edgeIndex);
			if (!node.addLogicalParameter(inter, true)) {
			    return;
            }

			fireTableCellUpdated(row, 0);
			fireTableCellUpdated(row, 1);
            fireTableRowsInserted(row, row);
		} else {
		    node.updateInteraction(row, edgeIndex);
			fireTableCellUpdated(row, 1);
		}
	}

	/**
	 * remove a selection of rows
	 *
	 * @param selectedrows
	 */
	public void removeInteractions(int[] selectedrows) {
		for (int i = selectedrows.length - 1; i >= 0; i--) {
			if (selectedrows[i] < interactions.size()) {
				interactions.remove(selectedrows[i]);
            }
		}
		fireTableDataChanged();
	}

    /**
     * @param index
     * @param to
     */
    public boolean moveElementAt(int index,int to) {
    	if (!interactions.moveElement(index, to)) {
    		return false;
    	}
        if (index < to) {
            fireTableRowsUpdated(index, to);
        } else {
            fireTableRowsUpdated(to, index);
        }
        return true;
    }

	public LogicalParameter getParameter(int row) {
		if (row < interactions.size()) {
			return (LogicalParameter) interactions.get(row);
		}
		return null;
	}
}
