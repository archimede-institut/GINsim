package fr.univmrs.ibdm.GINsim.dynamicGraph;

import java.awt.Color;
import java.awt.Component;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
/**
 * modified cell renderer to highlight states not in the graph
 */
public class GsDynamicPathItemCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 1L;

    private Vector v_in;
    private boolean[] t_in;
    private Color defaultColor;
    private Color highlightColor;
    
    /**
     * @param v_in
     */
    public GsDynamicPathItemCellRenderer(Vector v_in) {
        this(null, v_in, Color.WHITE, Color.RED);
    }
    
    /**
     * @param t_in
     */
    public GsDynamicPathItemCellRenderer(boolean[] t_in) {
        this(t_in, null, Color.WHITE, Color.RED);
    }
    
    /**
     * @param t_in
     * @param v_in
     * @param defaultColor 
     * @param highlightColor 
     */
    public GsDynamicPathItemCellRenderer(boolean[] t_in, Vector v_in, Color defaultColor, Color highlightColor) {
        super();
        this.t_in = t_in;
        this.v_in = v_in;
        this.defaultColor = defaultColor;
        this.highlightColor = highlightColor;
    }
    
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        Component cmp = super.getTableCellRendererComponent( table , value , isSelected , hasFocus , row , column );
        cmp.setBackground(defaultColor);
        if( table != null) {
            if (t_in != null) {
                if (!t_in[row]) {
                    cmp.setBackground( highlightColor );
                }
            } else {
                if (v_in.get(row) == Boolean.FALSE) {
                    cmp.setBackground( highlightColor );
                }
            }
        }
        return cmp;
    }
}
