package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel;

import java.util.Iterator;
import java.util.List;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryEdge;

public class GsTreeParam extends GsTreeElement {
  private List edgeIndexes;
  private boolean error, warning;

  public GsTreeParam(GsTreeElement parent, List v) {
    super(parent);
    this.edgeIndexes = v;
    error = warning = false;
  }
  public GsTreeParam(GsTreeElement parent) {
    super(parent);
    edgeIndexes = null;
  }
  public String toString() {
    String s = "";
    GsRegulatoryEdge ei;
    if (edgeIndexes != null) {
      for (Iterator it = edgeIndexes.iterator(); it.hasNext(); ) {
        ei = (GsRegulatoryEdge)it.next();
        s = s + " " + ei.me.getEdge(ei.index).getShortInfo("#");
      }
    }
    return s.trim();
  }
  public List getEdgeIndexes() {
    return edgeIndexes;
  }
  public void setEdgeIndexes(List v) {
    edgeIndexes = v;
  }
  public void setError(boolean b) {
    error = b;
  }
  public boolean isError() {
    return error;
  }
  public void setWarning(boolean b) {
    warning = b;
  }
  public boolean isWarning() {
    return warning;
  }
  public boolean isBasal() {
    return edgeIndexes == null;
  }
  public int compareTo(Object o) {
    GsTreeElement element = (GsTreeElement)o;
    if (toString().equals(element.toString()) && toString().equals("") && element instanceof GsTreeParam) {
      if (isBasal() && ((GsTreeParam)element).isBasal()) {
        return 0;
      }
      return 1;
    }
    return toString().compareTo(element.toString());
  }
}
