package fr.univmrs.tagc.GINsim.gui.tbclient.genetree;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

public class GeneTreeCellRenderer extends DefaultTreeCellRenderer {
  public GeneTreeCellRenderer() {
    super();
  }
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
    return ((AbstractTreeElement)value).getRendererComponent(sel);
   // return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
  }
}
