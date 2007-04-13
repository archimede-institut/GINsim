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
}
