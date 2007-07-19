package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser;

public abstract class TBinaryOperator extends TBooleanOperator {
  protected TBooleanTreeNode leftArg, rightArg;

  public TBinaryOperator() {
    super();
  }
  public void setArgs(TBooleanTreeNode leftNode, TBooleanTreeNode rightNode) {
    leftArg = leftNode;
    rightArg = rightNode;
  }
  public String toString() {
    return "(" + leftArg.toString() + " " + getSymbol() + " " + rightArg.toString() + ")";
  }
  public int getNbArgs() {
    return 2;
  }
  public TBooleanTreeNode[] getArgs() {
    TBooleanTreeNode[] r = new TBooleanTreeNode[2];
    r[0] = leftArg;
    r[1] = rightArg;
    return r;
  }
}
