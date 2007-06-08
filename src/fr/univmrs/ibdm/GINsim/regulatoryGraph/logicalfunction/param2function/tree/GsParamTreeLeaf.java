package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.param2function.tree;

public class GsParamTreeLeaf implements GsParamTreeElement {
  private int value;

  public GsParamTreeLeaf() {
    super();
    value = 0;
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
  public void print(int depth) {
    for (int i = 0; i < (2 * depth); i++) System.out.print(" ");
    System.out.println(value);
  }
  public void setValue(int v) {
    value = v;
  }
  public void addSon(GsParamTreeElement el, int index) {

  }
}
