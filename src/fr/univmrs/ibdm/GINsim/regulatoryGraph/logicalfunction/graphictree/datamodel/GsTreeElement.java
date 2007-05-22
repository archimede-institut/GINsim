package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel;

import java.util.Vector;
import java.awt.Color;
import java.util.Hashtable;

public abstract class GsTreeElement implements Comparable {
  protected Vector childs;
  protected GsTreeElement parent;
  protected boolean checked, selected;
  protected Color foreground;
  protected Hashtable property;

  public GsTreeElement(GsTreeElement parent) {
    childs = new Vector();
    this.parent = parent;
    checked = true;
    selected = false;
    foreground = Color.black;
    property = new Hashtable();
  }
  public GsTreeElement getChild(int index) {
    GsTreeElement te = null;
    int i = 0, n = 0;
    if (childs != null) {
      do {
        te = (GsTreeElement)childs.elementAt(i++);
        if (te.isChecked()) n++;
      } while (n <= index);
    }
    return te;
  }
  public void addChild(GsTreeElement element) {
    if ((childs != null) && (element != null))
      if (!childs.contains(element)) childs.addElement(element);
  }
  public void removeChild(int index) {
    if (childs != null)
      if (childs.size() > index)
        childs.removeElementAt(index);
  }
  public void clearChilds() {
    if (childs != null) childs.clear();
  }
  public int getChildCount() {
    int n = 0;
    GsTreeElement te;
    if (childs != null) {
      for (int i = 0; i < childs.size(); i++) {
        te = (GsTreeElement)childs.elementAt(i);
        if (te.isChecked()) n++;
      }
    }
    return n;
  }
  public void setForeground(Color c) {
    foreground = c;
  }
  public Color getForeground() {
    return foreground;
  }
  public boolean isLeaf() {
    return (getChildCount() == 0);
  }
  public boolean isChecked() {
    return checked;
  }
  public boolean isSelected() {
    return selected;
  }
  public void setSelected(boolean b) {
    selected =b;
  }
  public void setChecked(boolean b) {
    checked = b;
  }
  public GsTreeElement getParent() {
    return parent;
  }
  public void remove() {
    if (parent != null) {
      for (int i = 0; i < parent.getChildCount(); i++) {
        if (parent.getChild(i).equals(this)) {
          parent.removeChild(i);
          if (parent.getChildCount() == 0)
            parent.remove();
          break;
        }
      }
    }
  }
  public int getDepth() {
    int d = 0;
    GsTreeElement p = parent;
    while (p != null) {
      p = p.getParent();
      d++;
    }
    return d;
  }
  public void setProperty(Object key, Object value) {
    property.put(key, value);
  }
  public Object getProperty(String key) {
    return property.get(key);
  }
  public boolean containsUnselectChild() {
    for (int i = 0; i < childs.size(); i++)
      if (!((GsTreeElement)childs.elementAt(i)).isChecked()) return true;
    return false;
  }
  public Vector getUnselectChilds() {
    Vector v = new Vector();
    for (int i = 0; i < childs.size(); i++)
      if (!((GsTreeElement)childs.elementAt(i)).isChecked()) v.addElement(childs.elementAt(i));
    return v;
  }
  public abstract String toString();
  public int compareTo(Object o) {
    GsTreeElement element = (GsTreeElement)o;
    return toString().compareTo(element.toString());
  }
  public boolean equals(Object o) {
    return compareTo(o) == 0;
  }
}
