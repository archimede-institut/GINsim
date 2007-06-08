package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.param2function.tree;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;

public class GsParamTreeNode implements GsParamTreeElement {
  private GsRegulatoryVertex vertex;
  private GsParamTreeElement[] sons;
  private GsRegulatoryMultiEdge edge;

  public GsParamTreeNode(GsRegulatoryVertex v, GsRegulatoryMultiEdge me) {
    super();
    vertex = v;
    edge = me;
    sons = new GsParamTreeElement[edge.getEdgeCount() + 1];
    for (int i = 0; i <= edge.getEdgeCount(); i++) sons[i] = null;
  }
  public int getNbEdge() {
    return edge.getEdgeCount();
  }
  public int getNbSons() {
    return sons.length;
  }
  public boolean isLeaf() {
    return false;
  }
  public GsParamTreeElement getSon(int index) {
    return sons[index];
  }
  public void print(int depth) {
    for (int i = 0; i < (2 * depth); i++) System.out.print(" ");
    System.out.println(vertex.getId());
    for (int i = 0; i < sons.length; i++) sons[i].print(depth + 1);
  }
  public void addSon(GsParamTreeElement el, int index) {
    sons[index] = el;
  }
}
