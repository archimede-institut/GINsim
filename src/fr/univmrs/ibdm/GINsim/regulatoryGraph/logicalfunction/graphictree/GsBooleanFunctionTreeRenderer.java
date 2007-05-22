package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import javax.swing.tree.*;
import javax.swing.JTree;
import java.awt.Component;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import java.util.Enumeration;

public class GsBooleanFunctionTreeRenderer extends DefaultTreeCellRenderer implements ComponentListener {
  private int width;
  private JTree tree = null;

  public GsBooleanFunctionTreeRenderer(int totalWidth) {
    super();
    width = totalWidth - 140;
  }
  public int getWidth() {
    return width;
  }
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                boolean leaf, int row, boolean hasFocus) {
    this.tree = tree;
    ((GsTreeElement)value).setSelected(sel);
    return GsPanelFactory.getPanel((GsTreeElement)value, tree, sel, width, tree.getSelectionPaths());
  }
  public void componentHidden(ComponentEvent e) {}
  public void componentMoved(ComponentEvent e) {}
  public void componentResized(ComponentEvent e) {
    width = e.getComponent().getWidth() - 140;
    if (tree != null) {
      tree.stopEditing();
      int[] sr = tree.getSelectionRows();
      Enumeration enu = tree.getExpandedDescendants(new TreePath((GsTreeElement)tree.getModel().getRoot()));
      ((GsTreeInteractionsModel)tree.getModel()).fireTreeStructureChanged((GsTreeElement)tree.getModel().getRoot());
      if (enu != null) while (enu.hasMoreElements()) tree.expandPath((TreePath)enu.nextElement());
      tree.setSelectionRows(sr);
    }
  }
  public void componentShown(ComponentEvent e) {}
}
