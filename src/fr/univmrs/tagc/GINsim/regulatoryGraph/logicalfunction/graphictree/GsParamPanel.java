package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JLabel;
import javax.swing.JTree;

import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;

public class GsParamPanel extends GsBooleanFunctionTreePanel implements MouseListener, MouseMotionListener {
  private static final long serialVersionUID = -7863256897019020183L;
  private JLabel label;
  public GsParamPanel(GsTreeElement value, JTree tree, boolean sel, int width) {
    super(value, tree, sel, width);
    if (value.toString().equals("")) {
		label = new JLabel("          ");
	} else {
		label = new JLabel(value.toString());
	}
    label.setFont(defaultFont);
    label.setPreferredSize(new Dimension(width, charHeight));
    if (sel) {
      label.setBackground(Color.yellow);
      setBackground(Color.yellow);
    } else {
      label.setBackground(Color.white);
      setBackground(Color.white);
    }
    label.setForeground(value.getForeground());
    add(label, BorderLayout.CENTER);
  }
  public void mouseClicked(MouseEvent e) {

  }
  public void mouseEntered(MouseEvent e) {

  }
  public void mouseExited(MouseEvent e) {

  }
  public void mousePressed(MouseEvent e) {
    e.setSource(this);
    getMouseListener().mousePressed(e);
  }
  public void mouseReleased(MouseEvent e) {
    e.setSource(this);
    getMouseListener().mouseReleased(e);
  }
  public void mouseMoved(MouseEvent e) {
    e.setSource(this);
    getMouseMotionListener().mouseMoved(e);
  }
  public void mouseDragged(MouseEvent e) {
    e.setSource(this);
    getMouseMotionListener().mouseDragged(e);
  }
}