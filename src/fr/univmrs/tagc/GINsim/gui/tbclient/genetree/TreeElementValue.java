package fr.univmrs.tagc.GINsim.gui.tbclient.genetree;

import java.util.Vector;

import javax.swing.JLabel;

public class TreeElementValue extends TreeElementDeco {
  protected String value;
  public TreeElementValue(AbstractTreeElement e, String v) {
    super(e);
    value = v;
  }
  public TreeElementValue(AbstractTreeElement e, String v, Object o) {
    this(e, v);
    userObject = o;
  }
  public Vector getGraphicComponents(boolean sel) {
    Vector v = treeElement.getGraphicComponents(sel);
    JLabel l = new JLabel(value);
    l.setBackground(sel ? selColor : bgColor);
    l.setForeground(fgColor);
    l.setOpaque(true);
    v.addElement(l);
    return v;
  }

  public String toString() {
    return treeElement.toString() + value;
  }
  public String getValue() {
    return value;
  }
  public void concat(String s) {
    value += s;
  }
}
