package fr.univmrs.ibdm.GINsim.gui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * modified listCellRenderer: highlight some rows.
 */
public class GsListCellRenderer extends DefaultTreeCellRenderer {
    private static final long serialVersionUID = 296160062672716600L;
    
    /**
     * Empty constructor if you add GsColorable Objects to your list
     */
    public GsListCellRenderer() {
        super();
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
      Component cmp = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
      if (value instanceof GsColorable) {
          cmp.setBackground(((GsColorable)value).getColor());
          return cmp;
      }
      cmp.setBackground(Color.BLUE);
      return cmp;
    }
    
    
    
//    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
//        Component cmp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
//        if (value instanceof GsColorable) {
//            cmp.setBackground(((GsColorable)value).getColor());
//            return cmp;
//        }
//        cmp.setBackground(Color.BLUE);
//        return cmp;
//    }
}
