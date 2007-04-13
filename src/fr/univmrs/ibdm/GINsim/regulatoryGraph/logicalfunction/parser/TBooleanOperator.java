package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser;

public abstract class TBooleanOperator implements TBooleanTreeNode {
  protected String returnClassName;
  protected TBooleanParser parser;

  public TBooleanOperator() {
    super();
  }
  public void setReturnClass(String cl) {
    returnClassName = cl;
  }
  public void setParser(TBooleanParser parser) {
    this.parser = parser;
  }
  public abstract String getSymbol();
}
