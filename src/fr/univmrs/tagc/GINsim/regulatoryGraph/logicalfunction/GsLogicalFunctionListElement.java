package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;

public class GsLogicalFunctionListElement implements Comparable {
  private GsRegulatoryMultiEdge edge;
  private int index;

  public GsLogicalFunctionListElement(GsRegulatoryMultiEdge e, int k) {
    edge = e;
    index = k;
  }
  public String toString() {
    if (index == -1) {
		return "";
	}
    return edge.getId(index);
  }
  public GsRegulatoryMultiEdge getEdge() {
    return edge;
  }
  public int getIndex() {
    return index;
  }
  public boolean equals(Object o) {
    GsLogicalFunctionListElement f = (GsLogicalFunctionListElement)o;
    return f.toString().equals(toString()); // f.getIndex() == index;
  }
  public int compareTo(Object o) {
    GsLogicalFunctionListElement f = (GsLogicalFunctionListElement)o;
    return f.toString().compareTo(toString()); // f.getIndex() - index;
  }
}
