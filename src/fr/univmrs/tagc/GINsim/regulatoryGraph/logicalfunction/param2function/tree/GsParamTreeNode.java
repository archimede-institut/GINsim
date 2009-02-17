package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.param2function.tree;

import java.util.Hashtable;
import java.util.Vector;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;

public class GsParamTreeNode implements GsParamTreeElement {
  private GsRegulatoryVertex vertex;
  private GsParamTreeElement[] sons;
  private GsRegulatoryMultiEdge edge;
  private GsParamTreeNode parent;
  private int parentIndex;

  public GsParamTreeNode(GsRegulatoryVertex v, GsRegulatoryMultiEdge me, GsParamTreeNode p, int pi) {
    super();
    vertex = v;
    edge = me;
    parent = p;
    parentIndex = pi;
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
  public String toString() {
    return vertex.getId();
  }
  public boolean equals(Object e2) {
    boolean b = true;
    if (e2 instanceof GsParamTreeNode) {
      for (int i = 0; i < ((GsParamTreeNode)e2).getNbSons(); i++)
        b = b && getSon(i).equals(((GsParamTreeNode)e2).getSon(i));
      return b;
    }
    return false;
  }
  public GsParamTreeNode getParent() {
    return parent;
  }
  public int getParentIndex() {
    return parentIndex;
  }
  public void setSon(int i, GsParamTreeElement e) {
    sons[i] = e;
  }
  public void setParent(GsParamTreeNode e) {
    parent = e;
  }
  public void setParentIndex(int i) {
    parentIndex = i;
  }
  public void makeFunctions(Hashtable h, String f, int dv, boolean pattern) {
    String and = "";
    if (!pattern && (parent != null)) and = " & ";

    sons[0].makeFunctions(h, f + and + "!" + toString(), dv, pattern);
    for (int i = 1; i < sons.length; i++)
      sons[i].makeFunctions(h, f + and + toString() + ":" + (edge.getMin(i - 1)), dv, pattern);
  }
  public void makeDNF(Vector v, String s, int value) {
    String and = "";
    if (!s.equals("")) and = " & ";
    sons[0].makeDNF(v, s + and + "!" + toString(), value);
    for (int i = 1; i < sons.length; i++)
      //if (sons.length > 1)
        sons[i].makeDNF(v, s + and + toString() + ":" + (edge.getMin(i - 1)), value);
      //else
      //  sons[i].makeDNF(v, s + and + toString(), value);
  }
  public int hashCode() {
    return vertex.getId().hashCode();
  }
}
