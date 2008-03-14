package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.parser;

public abstract class TBooleanOperand implements TBooleanTreeNode {
  protected String value;
  protected String returnClass;
  protected TBooleanParser parser;

  public TBooleanOperand() {
    super();
  }
  public void setReturnClass(String cl) {
    returnClass = cl;
  }
  public void setValue(String val) {
    value = val;
  }
  public void setParser(TBooleanParser parser) {
    this.parser = parser;
  }
  public boolean isLeaf() {
    return true;
  }
  public String getVal() {
    return value;
  }
  public String toString() {
	  return toString(false);
  }
}
