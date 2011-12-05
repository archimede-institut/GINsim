package org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.datamodel;


public class TreeValue extends TreeElement {
  private byte value;

  public TreeValue(TreeElement parent, byte value) {
    super(parent);
    this.value = value;
  }
  public String toString() {
    return String.valueOf(value);
  }
  public int getValue() {
    return value;
  }
  public void setValue(byte v) {
    value = v;
  }
  public void remove() {
    super.remove(false);
    parent.setProperty("add", new Boolean(true));
  }

  public TreeElement addChild(TreeElement element, int index) {
    if (childs != null && !childs.contains(element)) {
		return super.addChild(element, index);
	}
    return null;
  } 
}
