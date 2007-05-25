package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.dnd;

import java.awt.event.MouseEvent;
import java.awt.Component;
import java.awt.image.BufferedImage;
import java.awt.Graphics;
import javax.swing.SwingUtilities;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.awt.event.MouseAdapter;

public class GsComponentAdapter extends MouseAdapter {
  protected GsGlassPane glassPane;
  protected String action;
  private List listeners;

  public GsComponentAdapter(GsGlassPane glassPane, String action) {
    this.glassPane = glassPane;
    this.action = action;
    this.listeners = new ArrayList();
  }
  public void mousePressed(MouseEvent e) {
    Component c = e.getComponent();

    BufferedImage image = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB);
    Graphics g = image.getGraphics();
    c.paint(g);

    glassPane.setVisible(true);

    Point p = (Point) e.getPoint().clone();
    SwingUtilities.convertPointToScreen(p, c);
    SwingUtilities.convertPointFromScreen(p, glassPane);

    glassPane.setPoint(p);
    glassPane.setOffsets(e.getX(), e.getY());
    glassPane.setImage(image);
    glassPane.repaint();
  }
  public void mouseReleased(MouseEvent e) {
    Component c = e.getComponent();

    Point p = (Point) e.getPoint().clone();
    SwingUtilities.convertPointToScreen(p, c);

    Point eventPoint = (Point) p.clone();
    SwingUtilities.convertPointFromScreen(p, glassPane);

    glassPane.setPoint(p);
    glassPane.setVisible(false);
    glassPane.setImage(null);

    fireGhostDropEvent(new GsDropEvent(action, eventPoint));
  }
  public void addGhostDropListener(GsDropListener listener) {
    if (listener != null)
      listeners.add(listener);
  }
  public void removeGhostDropListener(GsDropListener listener) {
    if (listener != null)
      listeners.remove(listener);
  }

  protected void fireGhostDropEvent(GsDropEvent evt) {
    Iterator it = listeners.iterator();
    while (it.hasNext()) {
      ((GsDropListener) it.next()).dropped(evt);
    }
  }
}
