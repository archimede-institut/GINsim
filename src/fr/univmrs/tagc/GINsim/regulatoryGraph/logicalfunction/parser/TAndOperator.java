package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.parser;

import java.util.ArrayList;
import java.util.List;

public class TAndOperator extends TBinaryOperator {
  public static final int priority = 0;
  protected static String SYMBOL = "&";

  public TAndOperator() {
    super();
  }
  public TBooleanData getValue() throws Exception {
    List leftData = leftArg.getValue().getData();
    List rightData = rightArg.getValue().getData();

    List andData = new ArrayList(leftData);
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
