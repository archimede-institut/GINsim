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
    ((GsTreeElement)value).setEdited(true);
    GsBooleanFunctionTreePanel p = GsPanelFactory.getPanel((GsTreeElement)value, tree, true,
                                   ((GsBooleanFunctionTreeRenderer)super.renderer).getWidth(), true);
    return p;
  }
  protected boolean canEditImmediately(EventObject event) {
    if ((event instanceof MouseEvent) && SwingUtilities.isLeftMouseButton((MouseEvent)event)) {
      MouseEvent me = (MouseEvent)event;
      TreePath tp = tree.getPathForLocation(me.getX(), me.getY());
      if (tp == null) return false;
      GsTreeElement treeElement = (GsTreeElement)tp.getLastPathComponent();
      if (treeElement == null)
        return false;
      else if (treeElement.isEdited())
        return true;
      if (treeElement.isLeaf()) { // parametres
        if (inHitRegion(me.getX(), me.getY()) && (me.getClickCount() > 0))
          return true;
        //else
        //  return false;
      }
      else if (tp.getParentPath() == tree.getPathForRow(0)) { // valeurs
        if (inHitRegion(me.getX(), me.getY()) && (me.getClickCount() > 0))
          return true;
        else if (!inHitRegion(me.getX(), me.getY()) && me.getClickCount() > 1)
          return true;
      }
      else if (tp == tree.getPathForRow(0)) { // racine
        if (inHitRegion(me.getX(), me.getY()) && (me.getClickCount() > 0))
          return true;
      }
      else { // fonctions
        if (inHitRegion(me.getX(), me.getY()) && (me.getClickCount() > 0))
          return true;
      }
      //else return true;
      //else //if ((me.getModifiers() & me.CTRL_MASK) != me.CTRL_MASK)
      //  return false; //(me.getClickCount() > 0);
    }
    return false;
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
        if (!leaf && ((x <= (bounds.x + offset - 16)) || (x > (bounds.x + offset + 14))) ||
            (y >= (bounds.y + 16)))
          return false;
        else if (leaf && ((x <= (bounds.x + offset - 16)) || (x > (bounds.x + offset + 3))))
          return false;
    }
    return true;
  }
}
