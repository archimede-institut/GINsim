package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser;

public interface TBooleanTreeNode {
  public TBooleanData getValue() throws Exception;
  public void setReturnClass(String cl);
  public void setParser(TBooleanParser parser);
}
