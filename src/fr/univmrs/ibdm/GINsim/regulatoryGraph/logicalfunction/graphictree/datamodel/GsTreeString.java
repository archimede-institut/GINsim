package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel;

public class GsTreeString extends GsTreeElement {
  private String string;

  public GsTreeString(GsTreeElement parent, String s) {
    super(parent);
    string = s;
  }
  public void setString(String s) {
    string = s;
  }
  public String toString() {
    return string;
  }

  public GsTreeElement addChild(GsTreeElement element, int index) {
    if (childs != null && !childs.contains(element)) {
		return super.addChild(element, index);
	}
    return null;
  } 
}
