package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel;


public class GsTreeValue extends GsTreeElement {
  private byte value;

  public GsTreeValue(GsTreeElement parent, byte value) {
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

  public GsTreeElement addChild(GsTreeElement element, int index) {
    if (childs != null && !childs.contains(element)) {
		return super.addChild(element, index);
	}
    return null;
  } 
}
