package org.ginsim.gui.graph.hierarchicaltransitiongraph;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.ginsim.gui.graph.dynamicgraph.DynamicItemCellRenderer;

public class HierarchicalCellRenderer extends DefaultTableCellRenderer implements TableCellRenderer {
	private static final long serialVersionUID = -7412224236522039621L;


    public Component getTableCellRendererComponent( JTable table , Object value , boolean isSelected , boolean hasFocus ,
            										int row , int column ) {
        Component cmp = super.getTableCellRendererComponent( table , value , isSelected , hasFocus , row , column );
        Color[] backgrounds = isSelected ? DynamicItemCellRenderer.BG_SEL : DynamicItemCellRenderer.BG_NORMAL;
        cmp.setBackground(backgrounds[0]);
        if (isSelected) {
            cmp.setForeground(Color.WHITE);
        } else {
            cmp.setForeground(Color.BLACK);
        }
        if(table != null) {
            String state = (String)table.getModel().getValueAt(row, column); 
            if (state.equals("*")) {
                cmp.setBackground( backgrounds[1] );
            } else if (state.startsWith("~")) {
                cmp.setBackground( Color.GRAY );
            }
        }
        return cmp;
    }
}
