package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;

public class GsBooleanFunctionTreeRenderer extends DefaultTreeCellRenderer implements ComponentListener {
private static final long serialVersionUID = 3456841880209526024L;
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
    ((GsTreeElement)value).setEdited(false);
    GsBooleanFunctionTreePanel p = GsPanelFactory.getPanel((GsTreeElement)value, tree, sel, width, false);
    return p;
  }
  public void componentHidden(ComponentEvent e) {}
  public void componentMoved(ComponentEvent e) {}
  public void componentResized(ComponentEvent e) {
    width = e.getComponent().getWidth() - 140;
    if (tree != null) {
      tree.stopEditing();
      int[] sr = tree.getSelectionRows();
      Enumeration enu = tree.getExpandedDescendants(new TreePath(tree.getModel().getRoot()));
      ((GsTreeInteractionsModel)tree.getModel()).fireTreeStructureChanged((GsTreeElement)tree.getModel().getRoot());
      if (enu != null) {
		while (enu.hasMoreElements()) {
			tree.expandPath((TreePath)enu.nextElement());
		}
	}
      tree.setSelectionRows(sr);
    }
  }
  public void componentShown(ComponentEvent e) {}
}
