package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser;

import java.util.Vector;

public class TOrOperator extends TBinaryOperator {
  public static final int priority = 0;
  protected static String SYMBOL = "|";

  public TOrOperator() {
    super();
  }
  public TBooleanData getValue() throws Exception {
    Vector leftData = leftArg.getValue().getData();
    Vector rightData = rightArg.getValue().getData();
    Vector orData = new Vector(leftData);
    for (int i = 0; i < rightData.size(); i++)
      if (!orData.contains(rightData.elementAt(i))) orData.addElement(rightData.elementAt(i));
    //orData.addAll(rightData);
    TBooleanData data = (TBooleanData)Class.forName(returnClassName).newInstance();
    data.setParser(parser);
    data.setData(orData);
    return data;
  }
  public String getSymbol() {
    return SYMBOL;
  }
}
