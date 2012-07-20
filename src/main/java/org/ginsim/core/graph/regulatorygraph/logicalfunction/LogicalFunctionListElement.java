package org.ginsim.core.graph.regulatorygraph.logicalfunction;

import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;

public class LogicalFunctionListElement implements Comparable {
  private RegulatoryMultiEdge edge;
  private int index;

  public LogicalFunctionListElement(RegulatoryMultiEdge e, int k) {
    edge = e;
    index = k;
  }
  public String toString() {
    if (index == -1) {
		return "";
	}
    return edge.getId(index);
  }
  public RegulatoryMultiEdge getEdge() {
    return edge;
  }
  public int getIndex() {
    return index;
  }
  public boolean equals(Object o) {
    LogicalFunctionListElement f = (LogicalFunctionListElement)o;
    return f.toString().equals(toString()); // f.getIndex() == index;
  }
  public int compareTo(Object o) {
    LogicalFunctionListElement f = (LogicalFunctionListElement)o;
    return f.toString().compareTo(toString()); // f.getIndex() - index;
  }
}
