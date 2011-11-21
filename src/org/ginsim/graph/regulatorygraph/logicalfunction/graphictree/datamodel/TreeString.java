package org.ginsim.graph.regulatorygraph.logicalfunction.graphictree.datamodel;

public class TreeString extends TreeElement {
  private String string;

  public TreeString(TreeElement parent, String s) {
    super(parent);
    string = s;
  }
  public void setString(String s) {
    string = s;
  }
  public String toString() {
    return string;
  }

  public TreeElement addChild(TreeElement element, int index) {
    if (childs != null && !childs.contains(element)) {
		return super.addChild(element, index);
	}
    return null;
  } 
}
