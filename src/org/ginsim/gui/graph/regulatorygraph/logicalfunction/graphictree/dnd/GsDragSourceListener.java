package org.ginsim.gui.graph.regulatorygraph.logicalfunction.graphictree.dnd;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import org.ginsim.graph.regulatorygraph.logicalfunction.graphictree.datamodel.TreeElement;
import org.ginsim.graph.regulatorygraph.logicalfunction.graphictree.datamodel.TreeExpression;
import org.ginsim.graph.regulatorygraph.logicalfunction.graphictree.datamodel.TreeValue;


public class GsDragSourceListener implements DragSourceListener {
  private GlassPane glassPane;
  private GsTransferable transferable;
  private JTree tree;

  public GsDragSourceListener(JTree tree, GlassPane gp) {
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
    Point p = dsde.getLocation();
    int action = dsde.getDropAction();
    DataFlavor choosen = transferable.getCurrentFlavor();
    if (action != DnDConstants.ACTION_COPY && action != DnDConstants.ACTION_MOVE) {
			dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveNoDrop);
		}
		else if (choosen != GsTransferable.MIXED_FLAVOR) {
      SwingUtilities.convertPointFromScreen(p, tree);
      TreePath tp = tree.getPathForLocation(p.x, p.y);
      if (tp != null) {
        TreeElement choosenElement = (TreeElement)tp.getLastPathComponent();
        if (choosen == GsTransferable.FUNCTION_FLAVOR && choosenElement instanceof TreeValue ||
        		//choosen == GsTransferable.PARAM_FLAVOR && choosenElement instanceof TreeValue) {
						choosen == GsTransferable.FUNCTION_FLAVOR && choosenElement instanceof TreeExpression) {
					if (action == DnDConstants.ACTION_COPY) {
						dsde.getDragSourceContext().setCursor(DragSource.DefaultCopyDrop);
					}
					else if (action == DnDConstants.ACTION_MOVE) {
						dsde.getDragSourceContext().setCursor(DragSource.DefaultMoveDrop);
					}
					p = (Point)dsde.getLocation().clone();
					SwingUtilities.convertPointToScreen(p, tree);
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
