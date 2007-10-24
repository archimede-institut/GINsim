package fr.univmrs.tagc.widgets;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import fr.univmrs.ibdm.GINsim.gui.GsColorable;

/**
 * modified listCellRenderer: highlight some rows.
 */
public class EnhancedListCellRenderer extends DefaultTreeCellRenderer {
    private static final long serialVersionUID = 296160062672716600L;
    
    /**
     * Empty constructor if you add GsColorable Objects to your list
     */
    public EnhancedListCellRenderer() {
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
}
