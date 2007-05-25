package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.dnd;

import javax.swing.JTree;
import java.awt.Point;
import javax.swing.SwingUtilities;
import java.awt.Rectangle;
import javax.swing.JOptionPane;

public class GsDropManager implements GsDropListener {
  private JTree tree;

  public GsDropManager(JTree tree) {
    this.tree = tree;
  }
  protected Point getTranslatedPoint(Point point) {
    Point p = (Point) point.clone();
    SwingUtilities.convertPointFromScreen(p, tree);
    return p;
  }
  protected boolean isInTarget(Point point) {
    Rectangle bounds = tree.getBounds();
    return bounds.contains(point);
  }
  public void dropped(GsDropEvent e) {
    String action = e.getAction();
    Point p = getTranslatedPoint(e.getDropLocation());

    if (isInTarget(p)) {
      JOptionPane.showMessageDialog(tree, "Action: " + action);
    }
  }
}
