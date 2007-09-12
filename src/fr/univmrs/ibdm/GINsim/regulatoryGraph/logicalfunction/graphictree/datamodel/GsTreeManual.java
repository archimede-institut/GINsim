package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel;


public class GsTreeManual extends GsTreeElement {
  public GsTreeManual(GsTreeElement parent) {
    super(parent);
  }
  public String toString() {
    return "Added parameters";
  }
  public boolean isLeaf() {
    return false;
  }
}
