package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel;


public class GsTreeValue extends GsTreeElement {
  private short value;

  public GsTreeValue(GsTreeElement parent, short value) {
    super(parent);
    this.value = value;
  }
  public String toString() {
    return String.valueOf(value);
  }
  public int getValue() {
    return value;
  }
  public void setValue(short v) {
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
