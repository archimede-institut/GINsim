package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.dnd;

import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseEvent;
import java.awt.Point;
import javax.swing.SwingUtilities;
import javax.swing.JTree;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.swing.tree.TreePath;

public class GsMotionAdapter extends MouseMotionAdapter {
  private GsGlassPane glassPane;

  public GsMotionAdapter(GsGlassPane glassPane) {
    this.glassPane = glassPane;
  }
  public void mouseDragged(MouseEvent e) {
    JTree c = (JTree)e.getComponent();
    TreePath[] sp = c.getSelectionPaths();
    Rectangle r;
    int minx = 100000, miny = 100000, maxx = 0, maxy = 0;

    Point pt = (Point)e.getPoint().clone();
    SwingUtilities.convertPointToScreen(pt, c);
    SwingUtilities.convertPointFromScreen(pt, glassPane);
    glassPane.setPoint(pt);

    BufferedImage image = null;
    BufferedImage image0 = new BufferedImage(c.getWidth(), c.getHeight(), BufferedImage.TYPE_INT_ARGB);
    c.paint(image0.getGraphics());

    if (!glassPane.isImageReady() && (sp != null)) {
      for (int i = 0; i < sp.length; i++) {
        r = c.getPathBounds(c.getSelectionPaths()[i]);
        if (minx > r.x) minx = r.x;
        if (maxx < (r.x + r.width)) maxx = r.x + r.width;
        if (maxy < (r.y + r.height)) maxy = r.y + r.height;
        if (miny > r.y) miny = r.y;
      }
      if ((maxy - miny + 1) > 200) {
        image = new BufferedImage(maxx - minx + 1, 200, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < sp.length; i++) {
          r = c.getPathBounds(c.getSelectionPaths()[i]);
          if ((r.y < (glassPane.getStartY() + 100)) || ((r.y + r.height - 1) > (glassPane.getStartY() - 100)))
            image.getGraphics().drawImage(image0.getSubimage(r.x, r.y, r.width, r.height),
                                          r.x - minx, 100 - glassPane.getStartY() + r.y, null);
        }
        glassPane.setOffsets(glassPane.getStartX() - minx, 100);
      }
      else {
        image = new BufferedImage(maxx - minx + 1, maxy - miny + 1, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < sp.length; i++) {
          r = c.getPathBounds(c.getSelectionPaths()[i]);
          image.getGraphics().drawImage(image0.getSubimage(r.x, r.y, r.width, r.height), r.x - minx, r.y - miny, null);
        }
        glassPane.setOffsets(glassPane.getStartX() - minx, glassPane.getStartY() - miny);
      }
      glassPane.setImage(image);
      glassPane.setVisible(true);
      glassPane.repaint();
    }

    Point p = (Point)e.getPoint().clone();
    SwingUtilities.convertPointToScreen(p, c);
    SwingUtilities.convertPointFromScreen(p, glassPane);
    glassPane.setPoint(p);
    glassPane.repaint();
  }
}
