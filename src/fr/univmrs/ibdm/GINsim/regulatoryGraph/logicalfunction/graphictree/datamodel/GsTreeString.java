package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel;

public class GsTreeString extends GsTreeElement {
  private String string;

  public GsTreeString(GsTreeElement parent, String s) {
    super(parent);
    string = s;
  }
  public String toString() {
    return string;
  }
}
