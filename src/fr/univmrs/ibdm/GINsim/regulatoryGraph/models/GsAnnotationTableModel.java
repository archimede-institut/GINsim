package fr.univmrs.ibdm.GINsim.regulatoryGraph.models;

import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import fr.univmrs.ibdm.GINsim.manageressources.Translator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsAnnotationPanel;

/**
 * model for the table displaying annotations (the linklist part).
 */
public class GsAnnotationTableModel extends AbstractTableModel {
	
	private static final long serialVersionUID = 3075357050742407362L;
	private Vector linkList = null;	
    GsAnnotationPanel panel;

	public int getColumnCount() {
		return 1;				
	}

	public int getRowCount() {
		if(linkList != null)		
		  return linkList.size()+1;
		return (0);  		
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		if(rowIndex >= 0 && columnIndex == 0){
			if(rowIndex == linkList.size())
				return "";
			return linkList.elementAt(rowIndex);
		}	 
		return null;
	}	

	public String getColumnName(int j) {
		if (j == 0) {
		    return Translator.getString("STR_url");
		}
		return "";					
	}
	
	public Class getColumnClass(int j) {
		if (j == 0) {
		    return String.class;
		}
		return Object.class;
	}
	
	public boolean isCellEditable(int row, int col) {
		 return true;
	}
	
	public void setValueAt(Object value, int row, int col) {
        boolean empty = (value.toString().trim().equals(""));
		if(row == linkList.size()) {
            if (!empty) {
    			linkList.add(row, value);
                fireTableCellUpdated(row, col);
                fireTableRowsInserted(row+1, row+1);
            }
		} else if (empty && row < linkList.size()) {
            linkList.remove(row);
            fireTableRowsDeleted(row, row);
        } else {
            linkList.setElementAt(value, row);
            fireTableCellUpdated(row, col);
        }
	}
	
	/**
	 * change the data to edit/display
	 * 
	 * @param linkList the new link list.
	 */
	public void setLinkList (Vector linkList) {
	    this.linkList = linkList;
	    fireTableDataChanged();
	}
}
