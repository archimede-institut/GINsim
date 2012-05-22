package org.ginsim.core.graph.regulatorygraph.logicalfunction.param2function.tree;

import java.util.Hashtable;
import java.util.Vector;

import org.ginsim.common.application.LogManager;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;



public class ParamTreeNode implements ParamTreeElement {
  private RegulatoryNode vertex;
  private ParamTreeElement[] sons;
  private RegulatoryMultiEdge edge;
  private ParamTreeNode parent;
  private int parentIndex;

  public ParamTreeNode(RegulatoryNode v, RegulatoryMultiEdge me, ParamTreeNode p, int pi) {
    super();
    vertex = v;
    edge = me;
    parent = p;
    parentIndex = pi;
    sons = new ParamTreeElement[edge.getEdgeCount() + 1];
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
  public ParamTreeElement getSon(int index) {
    return sons[index];
  }
  public void print(int depth) {
	  String result = "";
    for (int i = 0; i < (2 * depth); i++){
    	result += " ";
    }
    LogManager.trace( result + vertex.getId(), false);
    for (int i = 0; i < sons.length; i++){
    	sons[i].print(depth + 1);
    }
  }
  public void addSon(ParamTreeElement el, int index) {
    sons[index] = el;
  }
  public String toString() {
    return vertex.getId();
  }
  public boolean equals(Object e2) {
    boolean b = true;
    if (e2 instanceof ParamTreeNode) {
      for (int i = 0; i < ((ParamTreeNode)e2).getNbSons(); i++)
        b = b && getSon(i).equals(((ParamTreeNode)e2).getSon(i));
      return b;
    }
    return false;
  }
  public ParamTreeNode getParent() {
    return parent;
  }
  public int getParentIndex() {
    return parentIndex;
  }
  public void setSon(int i, ParamTreeElement e) {
    sons[i] = e;
  }
  public void setParent(ParamTreeNode e) {
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
