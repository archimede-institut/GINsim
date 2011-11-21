package org.ginsim.gui.tbclient.genetree;

import java.util.Vector;

public class TreeElementNode extends TreeElementDeco {
  protected Vector sons;

  public TreeElementNode(AbstractTreeElement e) {
    super(e);
    sons = new Vector();
  }
  public TreeElementNode(AbstractTreeElement e, Object o) {
    this(e);
    userObject = o;
  }
  public void addElement(AbstractTreeElement e) {
    sons.addElement(e);
  }
  public boolean isLeaf() {
    return false;
  }
  public int getChildCount() {
    return sons.size();
  }
  public Object getChild(int i) {
    if (i > (sons.size() - 1)) return null;
    return sons.elementAt(i);
  }
  public int indexOfChild(Object o) {
    return sons.indexOf(o);
  }
  public void check(boolean b) {
    super.check(b);
    for (int i = 0; i < getChildCount(); i++)
      ((AbstractTreeElement)getChild(i)).check(b);
  }
}
