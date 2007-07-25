package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.tree.*;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.*;

public class GsBooleanFunctionTreeEditor extends DefaultTreeCellEditor {
  private GsBooleanFunctionTreePanel p = null;

  public GsBooleanFunctionTreeEditor(JTree tree, DefaultTreeCellRenderer renderer) {
    super(tree, renderer);
  }
  public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected,
                                              boolean expanded, boolean leaf, int row) {
    ((GsTreeElement)value).setSelected(true);
    ((GsTreeElement)value).setEdited(true);
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
      p = GsPanelFactory.getPanel(treeElement, tree, true,
                                ((GsBooleanFunctionTreeRenderer)super.renderer).getWidth(), true);
      if (treeElement.isLeaf()) { // parametres
        if (inHitRegion(me.getX(), me.getY()) && (me.getClickCount() > 0))
          return true;
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
        if (!leaf) {
          if (treeElement.getParent() == null) {
            if ((x > (bounds.x + offset - 16)) && (x < (bounds.x + offset - 3)))
              return true;
            return false;
          }
          else if (treeElement instanceof GsTreeValue)
            return true;
          else if (treeElement instanceof GsTreeManual) {
            if ((x > (bounds.x + offset - 16)) && (x < (bounds.x + offset + 3)))
              return true;
          }
          else if (treeElement instanceof GsTreeExpression) {
            if (y < (bounds.y + 16))
              if ((x > (bounds.x + offset - 16)) && (x < (bounds.x + offset - 3)))
                return true;
              else if (p instanceof GsParamPanel)
                return true;
              else if ((p != null) && (x > (bounds.x + offset + 5)) && ((x < (bounds.x + offset + 12))) &&
                       (((GsFunctionPanel)p).isShowButtonEnabled()))
                return true;
            return false;
          }
        }
        else if (leaf && (treeElement.getParent() instanceof GsTreeExpression) && ((x <= (bounds.x + offset - 16)) || (x > (bounds.x + offset + 3))))
          return false;
        else if (leaf && (treeElement.getParent() instanceof GsTreeManual) && ((x <= (bounds.x + offset - 16)) || (x > (bounds.x + offset + 12))))
          return false;
    }
    return true;
  }
}
