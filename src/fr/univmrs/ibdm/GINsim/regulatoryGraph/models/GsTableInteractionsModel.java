package fr.univmrs.ibdm.GINsim.regulatoryGraph.models;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsLogicalParameter;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;

/**
 * This is the model behind the interaction editor
 */
public class GsTableInteractionsModel extends AbstractTableModel {

	private static final long serialVersionUID = -2093185924206743169L;

	//the vector of interaction
	private Vector interactions;

	//the current selected node
	private GsRegulatoryVertex node;
    private GsRegulatoryGraph graph;
    private Vector v_ok;

    /**
     * default constructor
     * 
     * @param graph
     * @param v_ok
     */
	public GsTableInteractionsModel(GsRegulatoryGraph graph, Vector v_ok) {
		super();
        this.v_ok = v_ok;
		this.interactions = null;
        this.graph = graph;
	}

	/**
	 * constructor with GsNodeRegulationData
	 * 
	 * @param no the currently selected node
	 */
	public GsTableInteractionsModel(GsRegulatoryVertex no) {
		super();
		node = no;
		this.interactions = node.getInteractions();
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
		if (interactions == null)
			return 0;
		return interactions.size() + 1;
	}

	/**
	 * @see javax.swing.table.TableModel#isCellEditable(int, int)
	 */
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		//only the first column is editable
		if (columnIndex == 0 && rowIndex < interactions.size()) {
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
			return new Integer(((GsLogicalParameter) interactions.get(rowIndex))
					.getValue());
		}
		return interactions.get(rowIndex).toString();
	}

	/**
	 * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
	 */
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (interactions == null)
			return;
		if (columnIndex == 0 && rowIndex >= 0 && rowIndex < interactions.size()) {
			//the first column
			int value = 1;
			if (aValue instanceof Integer)
				value = ((Integer) aValue).intValue();
			if (aValue instanceof String)
				value = Integer.parseInt((String) aValue);
			if (value <= node.getMaxValue() && value >= 0)
				((GsLogicalParameter) interactions.get(rowIndex)).setValue(value);
            fireTableCellUpdated(rowIndex, columnIndex);
		}
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnName(int)
	 */
	public String getColumnName(int columnIndex) {
		if (columnIndex == 0) {
			return Translator.getString("STR_InteractionValue");
		} 
		return Translator.getString("STR_ActiveInteractionEdgeList");
	}

	/* ** custom model ** */
	/**
	 * get all interactions
	 * 
	 * @return vector of interactions
	 */
	public Vector getInteractions() {
		return interactions;
	}

	/**
	 * remove Interactions at index row
	 * 
	 * @param row the row of the interaction, which will be removed
	 */
	public void removeInteractions(int row) {
		if (row < interactions.size()) {
			interactions.remove(row);
            v_ok.remove(row);
        }
		fireTableRowsDeleted(row, row);
	}

	/**
	 * set the current node
	 * 
	 * @param no the current node
	 */
	public void setNode(GsRegulatoryVertex no) {
		node = no;
		if (node != null)
			this.interactions = node.getInteractions();
		else
			this.interactions = null;
		fireTableDataChanged();
        v_ok.clear();
        for (int i=0 ; i<interactions.size() ; i++) {
            if ( !((GsLogicalParameter)interactions.get(i)).activable(graph, node) ) {
                v_ok.add(Boolean.FALSE);
            } else {
                v_ok.add(Boolean.TRUE);
            }
        }
        // one more for the empty line
        v_ok.add(Boolean.TRUE);
	}

	/**
	 * get the active(s) Edge(s) of the interaction at the row row.
	 * 
	 * @param row
	 * @return the active(s) Edge(s) of the interaction at the row row.
	 */
	public Vector getActivesEdges(int row) {
		if (row > -1 && row < interactions.size()) {
			return ((GsLogicalParameter) interactions.get(row)).getEdges();
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
		if (row >= interactions.size()) {
			GsLogicalParameter inter = new GsLogicalParameter(1);
			inter.setEdges(edgeIndex);
			if (!node.addInteraction(inter)) {
			    return;
            }
            if (  !inter.activable(graph, node) ) {
                v_ok.add(v_ok.size()-1, Boolean.FALSE);
            } else {
                v_ok.add(v_ok.size()-1, Boolean.TRUE);
            }

			fireTableCellUpdated(row, 0);
			fireTableCellUpdated(row, 1);
            fireTableRowsInserted(row, row);
		} else {
		    node.updateInteraction(row, edgeIndex);
            if ( !((GsLogicalParameter)interactions.get(row)).activable(graph, node) ) {
                v_ok.set(row, Boolean.FALSE);
            } else {
                v_ok.set(row, Boolean.TRUE);
            }
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
                v_ok.remove(selectedrows[i]);
            }
		}
		fireTableDataChanged();
	}

    /**
     * @param index
     * @param to
     */
    public void moveElementAt(int index,int to) {
        Object obj=interactions.remove(index);
        interactions.insertElementAt(obj,to);
        if (index < to) {
            fireTableRowsUpdated(index, to);
        } else {
            fireTableRowsUpdated(to, index);
        }
    }
}