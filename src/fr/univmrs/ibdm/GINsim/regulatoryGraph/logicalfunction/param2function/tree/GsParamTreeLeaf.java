package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.param2function.tree;

public abstract class GsParamTreeLeaf implements GsParamTreeElement {
  protected GsParamTreeNode parent;
  protected int parentIndex;

  public GsParamTreeLeaf(GsParamTreeNode p, int pi) {
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
  public GsParamTreeElement getSon(int index) {
    return null;
  }

  public abstract void print(int depth);
  public abstract String toString();
  public abstract void setValue(Object v);

  public void addSon(GsParamTreeElement el, int index) {

  }
  public GsParamTreeNode getParent() {
    return parent;
  }
  public int getParentIndex() {
    return parentIndex;
  }
  public void setParent(GsParamTreeNode e) {
    parent = e;
  }
  public void setParentIndex(int i) {
    parentIndex = i;
  }
}
