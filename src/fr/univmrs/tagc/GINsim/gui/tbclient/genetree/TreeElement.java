package fr.univmrs.tagc.GINsim.gui.tbclient.genetree;

import java.awt.Component;
import java.awt.FlowLayout;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TreeElement extends AbstractTreeElement {
  private String title;

  public TreeElement(String t) {
    super();
    title = t;
  }
  public TreeElement(String t, Object o) {
    this(t);
    userObject = o;
  }
  public Vector getGraphicComponents(boolean sel) {
    Vector v = new Vector();
    JLabel l = new JLabel(title);
    l.setBackground(bgColor);
    l.setOpaque(true);
    l.setForeground(fgColor);
    l.setBackground(sel ? selColor : bgColor);
    v.addElement(l);
    return v;
  }
  public Component getRendererComponent(boolean sel) {
    JPanel p = new JPanel();
    p.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    p.add((Component)getGraphicComponents(sel).firstElement());
    p.setForeground(fgColor);
    p.setBackground(sel ? selColor : bgColor);
    if (showBorder) p.setBorder(BorderFactory.createLineBorder(brdColor));
    return p;
  }
  public String toString() {
    return title;
  }
}
