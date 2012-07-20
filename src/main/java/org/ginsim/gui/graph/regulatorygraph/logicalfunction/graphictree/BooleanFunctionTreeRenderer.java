package org.ginsim.gui.graph.regulatorygraph.logicalfunction.graphictree;

import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreePath;

import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.TreeInteractionsModel;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.datamodel.TreeElement;


public class BooleanFunctionTreeRenderer extends DefaultTreeCellRenderer implements ComponentListener {
private static final long serialVersionUID = 3456841880209526024L;
private int width;
  private JTree tree = null;
  private static final int leftmargin = 90;
	private PanelFactory panelFactory;

  public BooleanFunctionTreeRenderer(int totalWidth, PanelFactory pf) {
    super();
    width = totalWidth - leftmargin;
		panelFactory = pf;
  }
  public int getWidth() {
    return width;
  }
  public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
                                                boolean leaf, int row, boolean hasFocus) {
    this.tree = tree;
    ((TreeElement)value).setSelected(sel);
    ((TreeElement)value).setEdited(false);
    BooleanFunctionTreePanel p = panelFactory.getPanel((TreeElement)value, tree, sel, width, false);
    p.updateSize();
    return p;
  }
  public void componentHidden(ComponentEvent e) {}
  public void componentMoved(ComponentEvent e) {}
  public void componentResized(ComponentEvent e) {
    width = e.getComponent().getWidth() - leftmargin;
    if (tree != null) {
      tree.stopEditing();
      int[] sr = tree.getSelectionRows();
      Enumeration enu = tree.getExpandedDescendants(new TreePath(tree.getModel().getRoot()));
      ((TreeInteractionsModel)tree.getModel()).fireTreeStructureChanged((TreeElement)tree.getModel().getRoot());
      if (enu != null) {
        while (enu.hasMoreElements()) {
          tree.expandPath((TreePath)enu.nextElement());
        }
      }
      tree.setSelectionRows(sr);
    }
  }
  public void componentShown(ComponentEvent e) {}
	public PanelFactory getPanelFactory() {
		return panelFactory;
	}
}
