package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Component;
import javax.swing.JTree;
import java.awt.event.ComponentListener;
import java.awt.event.ComponentEvent;
import javax.swing.tree.TreePath;
import java.util.Enumeration;

public class GsTreeInteractionsCellRenderer extends DefaultTreeCellRenderer implements ComponentListener {
  private int cellPanelWidth;
  private GsTreeCellPanel panel;
  private JTree tree = null;
  public GsTreeInteractionsCellRenderer(int w) {
    super();
    cellPanelWidth = w - 130;
  }
  public int getCellPanelWidth() {
    return cellPanelWidth;
  }
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                boolean leaf, int row, boolean hasFocus) {
    panel = new GsTreeCellPanel(value, leaf, row, null, sel, ((GsTreeElement)value).isSelected(), cellPanelWidth);

    this.tree = tree;
    return panel;
  }
  public void componentHidden(ComponentEvent e) {}
  public void componentMoved(ComponentEvent e) {}
  public void componentResized(ComponentEvent e) {
    cellPanelWidth = e.getComponent().getWidth() - 130;
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
