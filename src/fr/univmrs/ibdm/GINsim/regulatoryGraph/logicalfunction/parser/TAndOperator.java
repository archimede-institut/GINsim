package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser;

import java.util.Vector;

public class TAndOperator extends TBinaryOperator {
  public static final int priority = 0;
  protected static String SYMBOL = "&";

  public TAndOperator() {
    super();
  }
  public TBooleanData getValue() throws Exception {
    Vector leftData = ((TBooleanData)leftArg.getValue()).getData();
    Vector rightData = ((TBooleanData)rightArg.getValue()).getData();

    Vector andData = new Vector(leftData);
    andData.retainAll(rightData);
    TBooleanData data = (TBooleanData)Class.forName(returnClassName).newInstance();
    data.setParser(parser);
    data.setData(andData);
    return data;
  }
  public String getSymbol() {
    return SYMBOL;
  }
}
