package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.dnd;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class GsGlassPane extends JPanel {
  private AlphaComposite composite;
  private BufferedImage dragged = null;
  private Point location = new Point(0, 0);
  private int dx, dy, startx, starty;
  private boolean imageReady;

  public GsGlassPane() {
    setOpaque(false);
    composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
    imageReady = false;
  }
  public void setImage(BufferedImage dragged) {
    this.dragged = dragged;
  }
  public void setStartPosition(int x, int y) {
    startx = x;
    starty = y;
  }
  public int getStartX() {
    return startx;
  }
  public int getStartY() {
    return starty;
  }
  public void setPoint(Point location) {
    this.location = location;
  }
  public void setOffsets(int dx, int dy) {
    this.dx = dx;
    this.dy = dy;
  }
  public void paintComponent(Graphics g) {
    if (dragged == null) return;
    Graphics2D g2 = (Graphics2D)g;
    g2.setComposite(composite);
    g2.drawImage(dragged, (int)(location.getX() - dx), (int)(location.getY() - dy), null);
  }
  public void setImageReady(boolean ir) {
    imageReady = ir;
  }
  public boolean isImageReady() {
    return imageReady;
  }
}
