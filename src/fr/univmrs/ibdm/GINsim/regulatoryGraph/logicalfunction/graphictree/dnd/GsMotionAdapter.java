package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.dnd;

import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;
import java.awt.Component;
import java.awt.Point;
import javax.swing.SwingUtilities;

public class GsMotionAdapter extends MouseMotionAdapter {
  private GsGlassPane glassPane;

  public GsMotionAdapter(GsGlassPane glassPane) {
    this.glassPane = glassPane;
  }
  public void mouseDragged(MouseEvent e) {
    Component c = e.getComponent();
    Point p = (Point)e.getPoint().clone();
    SwingUtilities.convertPointToScreen(p, c);
    SwingUtilities.convertPointFromScreen(p, glassPane);
    glassPane.setPoint(p);
    glassPane.repaint();
  }
}
