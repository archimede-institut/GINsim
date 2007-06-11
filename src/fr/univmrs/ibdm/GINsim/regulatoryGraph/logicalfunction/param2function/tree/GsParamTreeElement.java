package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.param2function.tree;

interface GsParamTreeElement {
  public abstract boolean isLeaf();
  public abstract int getNbSons();
  public abstract GsParamTreeElement getSon(int index);
  public abstract void print(int depth);
  public abstract void addSon(GsParamTreeElement el, int index);
}
