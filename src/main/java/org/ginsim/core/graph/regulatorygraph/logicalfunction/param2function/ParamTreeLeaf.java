package org.ginsim.core.graph.regulatorygraph.logicalfunction.param2function;

public abstract class ParamTreeLeaf implements ParamTreeElement {
  protected ParamTreeNode parent;
  protected int parentIndex;

  public ParamTreeLeaf(ParamTreeNode p, int pi) {
    super();
    parent = p;
    parentIndex = pi;
  }
  public boolean isLeaf() {
    return true;
  }
  public int getNbSons() {
    return 0;
  }
  public ParamTreeElement getSon(int index) {
    return null;
  }

  public abstract void print(int depth);
  public abstract String toString();
  public abstract void setValue(Object v);

  public void addSon(ParamTreeElement el, int index) {

  }
  public ParamTreeNode getParent() {
    return parent;
  }
  public int getParentIndex() {
    return parentIndex;
  }
  public void setParent(ParamTreeNode e) {
    parent = e;
  }
  public void setParentIndex(int i) {
    parentIndex = i;
  }
}
