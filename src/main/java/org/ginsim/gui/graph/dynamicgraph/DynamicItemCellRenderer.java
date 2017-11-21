package org.ginsim.gui.graph.dynamicgraph;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * custom cell renderer to colorize cells
 */
public class DynamicItemCellRenderer extends DefaultTableCellRenderer {
    private static final long serialVersionUID = -2173326249965764544L;
    
    public static final Color[] BG_NORMAL = new Color[] { Color.WHITE, new Color(200, 255, 255), new Color(125,255,125)};
    public static final Color[] BG_SEL = new Color[] { new Color(75, 75, 75), new Color(125, 190, 190), new Color(60,190,60)};

    public Component getTableCellRendererComponent( JTable table , Object value , boolean isSelected , boolean hasFocus ,
            										int row , int column ) {
        Component cmp = super.getTableCellRendererComponent( table , value , isSelected , hasFocus , row , column );
        Color[] backgrounds = isSelected ? BG_SEL : BG_NORMAL;
        cmp.setBackground(backgrounds[0]);
        if (isSelected) {
            cmp.setForeground(Color.WHITE);
        } else {
            cmp.setForeground(Color.BLACK);
        }

        if( table != null && row >= 1) {
        	DynamicItemModel model = (DynamicItemModel)table.getModel();
            String s1 = model.getValueAt(0, column).toString(); 
            String s2 = model.getValueAt(row, column).toString(); 
            if (!s1.equals(s2)) {
            	if (model.isOutgoing(row)) {
                    cmp.setBackground(backgrounds[1]);
            	} else {
                    cmp.setBackground(backgrounds[2]);
            	}
            }
        }
        return cmp;
    }
}
