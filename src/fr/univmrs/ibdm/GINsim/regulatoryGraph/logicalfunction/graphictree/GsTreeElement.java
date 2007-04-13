package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import java.util.Vector;
import java.awt.Color;

public abstract class GsTreeElement implements Comparable {
  protected Vector childs;
  protected GsTreeElement parent;
  protected boolean selected;
  protected Color foreground;

  public GsTreeElement(GsTreeElement parent) {
    childs = new Vector();
    this.parent = parent;
    selected = true;
    foreground = Color.black;
  }
  public GsTreeElement getChild(int index) {
    if (childs != null)
      if (childs.size() > index)
        return (GsTreeElement)childs.elementAt(index);
    return null;
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
  public int getChildCount() {
    if (childs != null)
      return childs.size();
    return 0;
  }
  public void setForeround(Color c) {
    foreground = c;
  }
  public Color getForeground() {
    return foreground;
  }
  public boolean isLeaf() {
    return (getChildCount() == 0);
  }
  public boolean isSelected() {
    return selected;
  }
  public void setSelected(boolean b) {
    selected = b;
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
  public abstract String toString();
  public int compareTo(Object o) {
    GsTreeElement element = (GsTreeElement)o;
    return toString().compareTo(element.toString());
  }
  public boolean equals(Object o) {
    return compareTo(o) == 0;
  }
}
