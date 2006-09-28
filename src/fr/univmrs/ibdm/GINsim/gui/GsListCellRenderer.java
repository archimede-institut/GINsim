package fr.univmrs.ibdm.GINsim.gui;

import java.awt.Color;
import java.awt.Component;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

/**
 * modified listCellRenderer: highlight some rows.
 */
public class GsListCellRenderer extends DefaultListCellRenderer {
    private static final long serialVersionUID = 296160062672716600L;
    
    private Vector v_in;
    private boolean[] t_in;
    private Color defaultColor;
    private Color highlightColor;
    
    /**
     * @param v_in
     */
    public GsListCellRenderer(Vector v_in) {
        this(null, v_in, Color.WHITE, Color.RED);
    }
    
    /**
     * @param t_in
     */
    public GsListCellRenderer(boolean[] t_in) {
        this(t_in, null, Color.WHITE, Color.RED);
    }
    
    /**
     * @param t_in
     * @param v_in
     * @param defaultColor 
     * @param highlightColor 
     */
    public GsListCellRenderer(boolean[] t_in, Vector v_in, Color defaultColor, Color highlightColor) {
        super();
        this.t_in = t_in;
        this.v_in = v_in;
        this.defaultColor = defaultColor;
        this.highlightColor = highlightColor;
    }

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        Component cmp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        cmp.setBackground(defaultColor);
        if( list != null) {
            if (t_in != null) {
                if (!t_in[index]) {
                    cmp.setBackground( highlightColor );
                }
            } else {
                if (v_in.get(index) == Boolean.FALSE) {
                    cmp.setBackground( highlightColor );
                }
            }
        }
        return cmp;
    }
}
