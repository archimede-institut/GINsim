package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Component;
import javax.swing.tree.TreeCellRenderer;
import java.util.EventObject;
import java.awt.event.MouseEvent;
import javax.swing.SwingUtilities;
import java.awt.ComponentOrientation;
import java.awt.Rectangle;
import javax.swing.tree.TreePath;

public class GsTreeInteractionsCellEditor extends DefaultTreeCellEditor {
  public GsTreeInteractionsCellEditor(JTree tree, TreeCellRenderer renderer) {
    super(tree, (DefaultTreeCellRenderer)renderer);
  }
  public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded,
                                              boolean leaf, int row) {
    return new GsTreeCellPanel(value, leaf, row, tree, isSelected, ((GsTreeElement)value).isSelected(),
                               ((GsTreeInteractionsCellRenderer)super.renderer).getCellPanelWidth());
  }
  protected boolean canEditImmediately(EventObject event) {
    if((event instanceof MouseEvent) && SwingUtilities.isLeftMouseButton((MouseEvent)event)) {
      MouseEvent me = (MouseEvent)event;
      return ((me.getClickCount() > 0) && inHitRegion(me.getX(), me.getY()));
    }
    return (event == null);
  }
  protected boolean inHitRegion(int x, int y) {
    if(lastRow != -1 && tree != null) {
      Rectangle bounds = tree.getRowBounds(lastRow);
      ComponentOrientation treeOrientation = tree.getComponentOrientation();
      TreePath tp = tree.getPathForLocation(x, y);
      boolean leaf = false;
      if (tp != null) leaf = ((GsTreeElement)tp.getLastPathComponent()).isLeaf();
      if (treeOrientation.isLeftToRight()) {
        if (bounds != null)
          if (!leaf && ((x <= (bounds.x + offset - 13)) || (x > (bounds.x + offset + 18))))
            return false;
          else if (leaf && ((x <= (bounds.x + offset - 13)) || (x > (bounds.x + offset + 5))))
            return false;
      }
      else if ((bounds != null) && ((x < (bounds.x + bounds.width - offset + 15)) || (x > (bounds.x + bounds.width + 15)))) {
        return false;
      }
    }
    return true;
  }
}
