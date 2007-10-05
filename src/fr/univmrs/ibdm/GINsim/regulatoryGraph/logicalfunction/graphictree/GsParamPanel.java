package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JTree;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import javax.swing.JLabel;
import java.awt.Dimension;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeValue;

public class GsParamPanel extends GsBooleanFunctionTreePanel implements MouseListener, MouseMotionListener {
  private static final long serialVersionUID = -7863256897019020183L;
  private JLabel label;
  public GsParamPanel(GsTreeElement value, JTree tree, boolean sel, int width) {
    super(value, tree, sel, width);
    if (value.toString().equals(""))
      label = new JLabel("          ");
    else
      label = new JLabel(value.toString());
    label.setFont(defaultFont);
    label.setPreferredSize(new Dimension(width, charHeight));
    if (sel) {
      label.setBackground(Color.yellow);
      setBackground(Color.yellow);
    }
    else if (value.toString().equals("")) {
      if (((GsTreeInteractionsModel)tree.getModel()).getVertex().getBaseValue() == ((GsTreeValue)value.getParent().getParent()).getValue()) {
        label.setBackground(Color.cyan);
        setBackground(Color.cyan);
      }
      else {
        label.setBackground(Color.red);
        setBackground(Color.red);
      }
    }
    else {
      label.setBackground(Color.white);
      setBackground(Color.white);
    }
    label.setForeground(value.getForeground());
    add(label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.NORTHWEST,
                                      GridBagConstraints.NONE, new Insets(2, 5, 2, 0), 0, 0));
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
