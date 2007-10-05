package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.functioneditor;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;

public class GsListInteraction {
  private GsRegulatoryMultiEdge grme;
  private int index;

  public GsListInteraction(GsRegulatoryMultiEdge e, int i) {
    grme = e;
    index = i;
  }
  public GsRegulatoryMultiEdge getEdge() {
    return grme;
  }
  public int getIndex() {
    return index;
  }
  public String toString() {
    if (index >= 0)
      return grme.getId(index).replace('_', ' ') + " " + grme.getEdgeName(index).substring(0,
          grme.getEdgeName(index).indexOf(';')) + (grme.getSign(index) == 0 ? "+" : "-");
    return "!" + grme.getSource().getId();
  }
  public String stringValue() {
    int i = grme.getId(index).lastIndexOf((int)'_');
    if (index >= 0) {
      return grme.getId(index).substring(0, i) + "#" + grme.getId(index).substring(i + 1);
    }
    else {
      return "!" + grme.getId(index).substring(0, i);
    }
  }
}
