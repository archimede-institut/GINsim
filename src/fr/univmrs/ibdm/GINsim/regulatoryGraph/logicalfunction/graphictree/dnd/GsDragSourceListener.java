package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.dnd;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;

import javax.swing.*;
import javax.swing.tree.TreePath;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.*;

public class GsDragSourceListener implements DragSourceListener {
  private GsGlassPane glassPane;
  private GsTransferable transferable;
  private JTree tree;

  public GsDragSourceListener(JTree tree, GsGlassPane gp) {
    glassPane = gp;
    this.tree = tree;
  }
  public void setTransferable(GsTransferable t) {
    transferable = t;
  }
  public void dragDropEnd(DragSourceDropEvent dsde) {
    Point p = (Point) dsde.getLocation().clone();

    SwingUtilities.convertPointFromScreen(p, glassPane);
    glassPane.setPoint(p);
    glassPane.setVisible(false);
    glassPane.setImage(null);
  }

  public void dragEnter(DragSourceDragEvent dsde) {

  }

  public void dragExit(DragSourceEvent dse) {
    dse.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
    Point p = (Point)dse.getLocation().clone();
    SwingUtilities.convertPointFromScreen(p, glassPane);
    glassPane.setPoint(p);
    glassPane.repaint();
  }

  public void dragOver(DragSourceDragEvent dsde) {
    int action = dsde.getDropAction();
    DataFlavor choosen = transferable.getCurrentFlavor();
    if ((action != DnDConstants.ACTION_COPY) && (action != DnDConstants.ACTION_MOVE))
      dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
    else if (choosen != GsTransferable.MIXED_FLAVOR) {
      Point p = dsde.getLocation();
      SwingUtilities.convertPointFromScreen(p, tree);
      TreePath tp = tree.getPathForLocation(p.x, p.y);
      if (tp != null) {
        GsTreeElement choosenElement = (GsTreeElement)tp.getLastPathComponent();
        if (((choosen == GsTransferable.FUNCTION_FLAVOR) && (choosenElement instanceof GsTreeValue)) ||
            ((choosen == GsTransferable.VALUE_FLAVOR) && (choosenElement instanceof GsTreeString)) ||
            ((choosen == GsTransferable.PARAM_FLAVOR) && (choosenElement == transferable.getPParent()))) {
          if (action == DnDConstants.ACTION_COPY)
            dsde.getDragSourceContext().setCursor(DragSource.DefaultCopyDrop);
          else if (action == DnDConstants.ACTION_MOVE)
            dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
          p = (Point)dsde.getLocation().clone();
          SwingUtilities.convertPointFromScreen(p, glassPane);
          glassPane.setPoint(p);
          glassPane.repaint();
        }
      }
    }
  }

  public void dropActionChanged(DragSourceDragEvent dsde) {

  }
}
