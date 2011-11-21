package org.ginsim.gui.graph.hierarchicaltransitiongraph;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class HierarchicalCellRenderer extends DefaultTableCellRenderer implements TableCellRenderer {
	private static final long serialVersionUID = -7412224236522039621L;


    public Component getTableCellRendererComponent( JTable table , Object value , boolean isSelected , boolean hasFocus ,
            										int row , int column ) {
        Component cmp = super.getTableCellRendererComponent( table , value , isSelected , hasFocus , row , column );
        cmp.setBackground(Color.WHITE);
        if(table != null) {
            String state = (String)table.getModel().getValueAt(row, column); 
            if (state.equals("*")) {
            	cmp.setBackground( Color.green );
            }
        }
        return cmp;
    }
}
