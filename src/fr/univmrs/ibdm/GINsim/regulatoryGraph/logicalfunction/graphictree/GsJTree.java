package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.Autoscroll;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class GsJTree extends JTree implements Autoscroll {
  private int margin = 15;

  public GsJTree(TreeModel m) {
    super(m);
  }
  public void setSelectionPath(TreePath path) {
    if (!isPathSelected(path))
      super.setSelectionPath(path);
  }
  public void autoscroll(Point p) {
    int realrow = getRowForLocation(p.x, p.y);
    if (realrow != -1) {
      Rectangle outer = getBounds();
      realrow = ((p.y + outer.y) <= margin ? realrow < 1 ? 0 : realrow - 1 :
                 realrow < (getRowCount() - 1) ? realrow + 1 : realrow);
      scrollRowToVisible(realrow);
    }
  }

  public Insets getAutoscrollInsets() {
    Rectangle outer = getBounds();
    Rectangle inner = getParent().getBounds();
    return new Insets(inner.y - outer.y + margin, inner.x - outer.x + margin,
                      outer.height - inner.height - inner.y + outer.y + margin,
                      outer.width - inner.width - inner.x + outer.x + margin);
  }
}
