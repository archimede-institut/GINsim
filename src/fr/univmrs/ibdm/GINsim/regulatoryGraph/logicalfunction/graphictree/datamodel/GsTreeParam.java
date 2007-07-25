package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsEdgeIndex;
import java.util.Vector;
import java.util.Iterator;

public class GsTreeParam extends GsTreeElement {
  private Vector edgeIndexes;
  private boolean error, warning;

  public GsTreeParam(GsTreeElement parent, Vector v) {
    super(parent);
    this.edgeIndexes = v;
    error = warning = false;
  }
  public String toString() {
    String s = "";
    GsEdgeIndex ei;
    for (Iterator it = edgeIndexes.iterator(); it.hasNext(); ) {
      ei = (GsEdgeIndex)it.next();
      s = s + " " + ei.data.getId(ei.index);
    }
    return s.trim();
  }
  public Vector getEdgeIndexes() {
    return edgeIndexes;
  }
  public void setEdgeIndexes(Vector v) {
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
}
