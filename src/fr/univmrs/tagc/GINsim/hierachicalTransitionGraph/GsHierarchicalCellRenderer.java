package fr.univmrs.tagc.GINsim.hierachicalTransitionGraph;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class GsHierarchicalCellRenderer extends DefaultTableCellRenderer implements TableCellRenderer {
	private static final long serialVersionUID = -7412224236522039621L;


    public Component getTableCellRendererComponent( JTable table , Object value , boolean isSelected , boolean hasFocus ,
            										int row , int column ) {
        Component cmp = super.getTableCellRendererComponent( table , value , isSelected , hasFocus , row , column );
        cmp.setBackground(Color.WHITE);
        if(table != null) {
        	if (column == 0) {
        		cmp.setBackground(Color.gray);
        		return cmp;
        	}
            String state = (String)table.getModel().getValueAt(row, column-1); 
            if (state.equals("*")) {
            	cmp.setBackground( Color.green );
            }
        }
        return cmp;
    }
}
