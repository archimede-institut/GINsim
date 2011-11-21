package org.ginsim.graph.regulatorygraph.logicalfunction.parser;

public abstract class TBooleanOperator implements TBooleanTreeNode {
  protected String returnClassName;
  protected TBooleanParser parser;

  public TBooleanOperator() {
    super();
  }
  public String toString() {
	  return toString(false);
  }
  public void setReturnClass(String cl) {
    returnClassName = cl;
  }
  public void setParser(TBooleanParser parser) {
    this.parser = parser;
  }
  public boolean isLeaf() {
    return false;
  }
  public abstract String getSymbol();
  public abstract int getNbArgs();
  public abstract TBooleanTreeNode[] getArgs();
}
