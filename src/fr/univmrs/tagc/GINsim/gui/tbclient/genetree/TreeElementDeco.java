package fr.univmrs.tagc.GINsim.gui.tbclient.genetree;

import java.awt.Component;
import java.util.Vector;

import javax.swing.JPanel;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;

public abstract class TreeElementDeco extends AbstractTreeElement {
  protected AbstractTreeElement treeElement;

  public TreeElementDeco(AbstractTreeElement e) {
    super();
    treeElement = e;
  }
  public TreeElementDeco(AbstractTreeElement e, Object o) {
    this(e);
    userObject = o;
  }
  public void check(boolean b) {
    treeElement.check(b);
  }
  public int getChildCount() {
    return treeElement.getChildCount();
  }
  public boolean isLeaf() {
    return treeElement.isLeaf();
  }
  public Object getChild(int i) {
    return treeElement.getChild(i);
  }
  public int indexOfChild(Object o) {
    return treeElement.indexOfChild(o);
  }
  public String toString() {
    return treeElement.toString();
  }
  public void addElement(AbstractTreeElement e) {
    treeElement.addElement(e);
  }
  public Vector getGraphicComponents(boolean sel) {
    return treeElement.getGraphicComponents(sel);
  }
  public Component getRendererComponent(boolean sel) {
    Vector v = getGraphicComponents(sel);
    JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 3, 0));
    for (int i = 0; i < v.size(); i++) p.add((Component)v.elementAt(i));
    p.add(Box.createVerticalStrut(20));
    p.setOpaque(true);
    p.setBackground(sel ? selColor : bgColor);
    if (showBorder) p.setBorder(BorderFactory.createLineBorder(brdColor));
    return p;
  }
  public boolean isSelected() {
  	return (isSelected() || treeElement.isSelected());
  }
  public String getValue() {
  	return treeElement.getValue();
  }
}
