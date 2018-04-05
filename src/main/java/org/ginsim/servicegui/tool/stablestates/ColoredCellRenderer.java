package org.ginsim.servicegui.tool.stablestates;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * custom cell renderer to colorize cells
 */
@SuppressWarnings("serial")
public class ColoredCellRenderer extends DefaultTableCellRenderer {

    static final Color EVEN_BG = new Color(255, 255, 200);
    static final Color ODD_BG = new Color(220, 220, 150);
    static final Color STAR_BG = Color.CYAN;
    static final Color ACTIVE_BG = new Color(142, 142, 142);

    @Override
    public Component getTableCellRendererComponent(JTable table , Object value , boolean isSelected , boolean hasFocus ,
                                                   int row , int column ) {
        Component cmp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        if( table != null && row >= 0) {
            if (column == 0 || "0".equals(value)) {
                if (isSelected) {
                    cmp.setBackground(table.getSelectionBackground());
                } else {
                    cmp.setBackground(row%2 == 0 ? EVEN_BG : ODD_BG);
                }
                if (column == 0) {
                    cmp.setForeground(Color.BLACK);
                } else {
                    cmp.setForeground(cmp.getBackground());
                }
            } else if ("*".equals(value)) {
                cmp.setBackground(STAR_BG);
                cmp.setForeground(Color.BLACK);
            } else {
                cmp.setForeground(Color.BLACK);
                cmp.setBackground(ACTIVE_BG);
            }
        }
        return cmp;
    }
}
