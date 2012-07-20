package org.ginsim.core.graph.regulatorygraph.logicalfunction.parser;

public interface TBooleanTreeNode {
  public TBooleanData getValue() throws Exception;
  public void setReturnClass(String cl);
  public void setParser(TBooleanParser parser);
  public String toString(boolean par);
  public boolean isLeaf();
}
