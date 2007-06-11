package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;

public class GsLogicalFunctionListElement implements Comparable {
  private GsRegulatoryMultiEdge edge;
  private int index;

  public GsLogicalFunctionListElement(GsRegulatoryMultiEdge e, int k) {
    edge = e;
    index = k;
  }
  public String toString() {
    if (index == -1) return "";
    return edge.getId(index);
  }
  public GsRegulatoryMultiEdge getEdge() {
    return edge;
  }
  public int getIndex() {
    return index;
  }
  public int compareTo(Object o) {
    GsLogicalFunctionListElement f = (GsLogicalFunctionListElement)o;
    return f.toString().compareTo(toString());
  }
}
