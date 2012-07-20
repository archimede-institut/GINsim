package org.ginsim.gui.tbclient.genetree;

import java.awt.Color;
import java.awt.Component;
import java.util.Vector;

public abstract class AbstractTreeElement {
  protected Color fgColor, bgColor, brdColor, selColor;
  protected boolean showBorder;
  protected Object userObject;

  public AbstractTreeElement() {
    super();
    fgColor = Color.black;
    bgColor = Color.white;
    brdColor = Color.red;
    selColor = Color.yellow;
    showBorder = false;
    userObject = null;
  }
  public AbstractTreeElement(Object o) {
  	this();
  	userObject = o;
  }
  public abstract Component getRendererComponent(boolean sel);

  public abstract Vector getGraphicComponents(boolean sel);

  public boolean isLeaf() {
    return true;
  }
  public int getChildCount() {
    return 0;
  }
  public void check(boolean b) {

  }
  public void setNote(boolean b) {
  	
  }
  public Object getChild(int i) {
    return null;
  }
  public int indexOfChild(Object o) {
    return -1;
  }
  public String toString() {
    return "";
  }
  public void addElement(AbstractTreeElement e) {

  }
  public boolean equals(Object o) {
    return toString().equalsIgnoreCase(o.toString());
  }
  public void setFgColor(Color c) {
    fgColor = c;
  }
  public void setBgColor(Color c) {
    bgColor = c;
  }
  public void setShowBorder(boolean b) {
    showBorder = b;
  }
  public void setBorderColor(Color c) {
    brdColor = c;
  }
  public void setSelColor(Color c) {
  	selColor = c;
  }
  public Object getUserObject() {
  	return userObject;
  }
  public boolean isSelected() {
  	return false;
  }
  public String getValue() {
  	return "";
  }
}
