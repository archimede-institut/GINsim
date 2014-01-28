package org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree;

import java.awt.Color;
import java.util.Hashtable;
import java.util.Vector;

public abstract class TreeElement implements Comparable {
  protected Vector childs;
  protected TreeElement parent;
  protected boolean selected, editable, dropable, edited;
  protected Color foreground;
  protected Hashtable property;

  public TreeElement(TreeElement parent) {
    childs = new Vector();
    this.parent = parent;
    selected = false;
    foreground = Color.black;
    property = new Hashtable();
    editable = false;
    dropable = false;
    edited = false;
  }
  public void setParent(TreeElement parent) {
    this.parent = parent;
  }
  public TreeElement getChild(int index) {
    TreeElement te = null;
    if (childs != null) {
      if (index < childs.size()) {
		te = (TreeElement)childs.elementAt(index);
	}
    }
    return te;
  }

  public TreeElement addChild(TreeElement element, int index) {
    if (childs != null && element != null) {
		if (index == -1) {
			childs.add(element);
		} else {
			childs.insertElementAt(element, index);
		}
	}
    return element;
  }
  public void removeChild(int index) {
    if (childs != null) {
		if (childs.size() > index) {
			childs.removeElementAt(index);
		}
	}
  }
  public void clearChilds() {
    if (childs != null) {
		childs.clear();
	}
  }
  public Vector getChilds() {
    return childs;
  }
  public int getChildCount() {
    int n = 0;
    if (childs != null) {
      n = childs.size();
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
    return getChildCount() == 0 && getDepth() != 0;
  }
  public boolean isSelected() {
    return selected;
  }
  public boolean isEdited() {
    return edited;
  }
  public void setSelected(boolean b) {
    selected =b;
  }
  public void setEdited(boolean b) {
    edited =b;
  }
  public void setDropable(boolean b) {
    dropable = b;
  }
  public boolean isDropable() {
    return dropable;
  }
  public TreeElement getParent() {
    return parent;
  }
  public void remove(boolean removeParent) {
    if (parent != null) {
      for (int i = 0; i < parent.getChildCount(); i++) {
        if (parent.getChild(i) == this) {
          parent.removeChild(i);
          if (parent.getChildCount() == 0 && removeParent) {
			parent.remove(removeParent);
		}
          break;
        }
      }
    }
  }
  public int getDepth() {
    int d = 0;
    TreeElement p = parent;
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
  public void setEditable(boolean e) {
    editable = e;
  }
  public boolean isEditable() {
    return editable;
  }
  public abstract String toString();
  public int compareTo(Object o) {
    TreeElement element = (TreeElement)o;
    return toString().compareTo(element.toString());
  }
  public boolean equals(Object o) {
    return compareTo(o) == 0;
  }
  public void drop(TreeElement element) {
  }
}
