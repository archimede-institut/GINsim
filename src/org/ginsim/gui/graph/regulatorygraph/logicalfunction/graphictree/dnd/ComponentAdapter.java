package org.ginsim.gui.graph.regulatorygraph.logicalfunction.graphictree.dnd;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;

public class ComponentAdapter extends MouseAdapter {
  protected GlassPane glassPane;
  protected String action;

  public ComponentAdapter(GlassPane glassPane, String action) {
    this.glassPane = glassPane;
    this.action = action;
  }
  public void mousePressed(MouseEvent e) {
    glassPane.setImageReady(false);
    glassPane.setStartPosition(e.getX(), e.getY());
  }
  public void mouseReleased(MouseEvent e) {
    Component c = e.getComponent();
    Point p = (Point) e.getPoint().clone();
    SwingUtilities.convertPointToScreen(p, c);
    SwingUtilities.convertPointFromScreen(p, glassPane);

    glassPane.setPoint(p);
    glassPane.setVisible(false);
    glassPane.setImage(null);
    glassPane.setImageReady(false);
  }
}
