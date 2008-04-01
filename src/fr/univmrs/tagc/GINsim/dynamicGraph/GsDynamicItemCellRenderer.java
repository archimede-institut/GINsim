package fr.univmrs.tagc.GINsim.dynamicGraph;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * custom cell renderer to colorize cells
 */
public class GsDynamicItemCellRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = -2173326249965764544L;

    public Component getTableCellRendererComponent( JTable table , Object value , boolean isSelected , boolean hasFocus ,
            										int row , int column ) {
        Component cmp = super.getTableCellRendererComponent( table , value , isSelected , hasFocus , row , column );
        cmp.setBackground(Color.WHITE);
        if( table != null && row >= 1) {
        	GsDynamicItemModel model = (GsDynamicItemModel)table.getModel();
            String s1 = model.getValueAt(0, column).toString(); 
            String s2 = model.getValueAt(row, column).toString(); 
            if (!s1.equals(s2)) {
            	if (model.isOutgoing(row)) {
            		cmp.setBackground( Color.cyan );
            	} else {
            		cmp.setBackground( Color.green );
            	}
            }
        }
        return cmp;
    }
}
