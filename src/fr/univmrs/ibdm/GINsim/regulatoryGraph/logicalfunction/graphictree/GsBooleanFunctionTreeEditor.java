package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.*;
import javax.swing.tree.*;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;

public class GsBooleanFunctionTreeEditor extends DefaultTreeCellEditor {
  public GsBooleanFunctionTreeEditor(JTree tree, DefaultTreeCellRenderer renderer) {
    super(tree, renderer);
  }
  public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected,
                                              boolean expanded, boolean leaf, int row) {
    ((GsTreeElement)value).setSelected(true);
    return GsPanelFactory.getPanel((GsTreeElement)value, tree, true,
                                   ((GsBooleanFunctionTreeRenderer)super.renderer).getWidth());
  }
  protected boolean canEditImmediately(EventObject event) {
    if ((event instanceof MouseEvent) && SwingUtilities.isLeftMouseButton((MouseEvent)event)) {
      MouseEvent me = (MouseEvent)event;
      TreePath tp = tree.getPathForLocation(me.getX(), me.getY());
      GsTreeElement treeElement = (GsTreeElement)tp.getLastPathComponent();
      if (treeElement == null)
        return false;
      else if (treeElement.isLeaf()) {
        if (inHitRegion(me.getX(), me.getY()) && (me.getClickCount() > 0))
          return true;
        else
          return false;
      }
      else if (tp.getParentPath() == tree.getPathForRow(0)) {
        if (inHitRegion(me.getX(), me.getY()) && (me.getClickCount() > 0))
          return true;
        else if (!inHitRegion(me.getX(), me.getY()) && me.getClickCount() > 1)
          return true;
      }
      else {
        if (!inHitRegion(me.getX(), me.getY()) && (me.getClickCount() > 1))
          return true;
        else if (inHitRegion(me.getX(), me.getY()) && (me.getClickCount() > 0))
          return true;
        else
          return false;
      }
    }
    return (event == null);
  }
  protected boolean inHitRegion(int x, int y) {
    TreePath tp = tree.getPathForLocation(x, y);
    if (tp == null) return false;
    if (lastRow != -1) {
      Rectangle bounds = tree.getPathBounds(tp);
      GsTreeElement treeElement = (GsTreeElement)tp.getLastPathComponent();
      boolean leaf = treeElement.isLeaf();

      // Ajout pour compatibilite avec Java 1.4
      offset = 20;

      if (bounds != null)
        if (!leaf && ((x <= (bounds.x + offset - 16)) || (x > (bounds.x + offset + 14))))
          return false;
        else if (leaf && ((x <= (bounds.x + offset - 16)) || (x > (bounds.x + offset + 14))))
          return false;
    }
    return true;
  }
}
