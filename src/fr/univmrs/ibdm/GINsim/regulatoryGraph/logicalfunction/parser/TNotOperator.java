package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser;

import java.util.Vector;

public class TNotOperator extends TUnaryOperator {
  public static final int priority = 1;
  protected static String SYMBOL = "!";

  public TNotOperator() {
    super();
  }
  public TBooleanData getValue() throws Exception {
    Vector data = ((TBooleanData)arg.getValue()).getData();
    Vector notData = new Vector(parser.getAllData());
    notData.removeAll(data);
    TBooleanData d = (TBooleanData)Class.forName(returnClassName).newInstance();
    d.setData(notData);
    return d;
  }
  public String getSymbol() {
    return SYMBOL;
  }
}
