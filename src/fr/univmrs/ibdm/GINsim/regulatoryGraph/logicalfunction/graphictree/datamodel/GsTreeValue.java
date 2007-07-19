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
    super.remove();
    parent.setProperty("add", new Boolean(true));
  }
}
