package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser;

public abstract class TUnaryOperator extends TBooleanOperator {
  protected TBooleanTreeNode arg;

  public TUnaryOperator() {
    super();
  }
  public void setArg(TBooleanTreeNode node) {
    arg = node;
  }
}
