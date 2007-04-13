package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Component;
import javax.swing.JTree;
import javax.swing.JLabel;

public class GsTreeInteractionsCellRenderer extends DefaultTreeCellRenderer {
  public GsTreeInteractionsCellRenderer() {
    super();
  }
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                boolean leaf, int row, boolean hasFocus) {
   //JLabel oldlab = (JLabel)super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    return new GsTreeCellPanel(value, leaf, row, null /*oldlab.getIcon()*/, sel, ((GsTreeElement)value).isSelected());
  }
}
