package org.ginsim.core.graph.regulatorygraph.logicalfunction.parser;

import java.util.ArrayList;
import java.util.List;

public class TNotOperator extends TUnaryOperator {
  public static final int priority = 1;
  protected static String SYMBOL = "!";

  public TNotOperator() {
    super();
  }
  public TBooleanData getValue() throws Exception {
    List data = arg.getValue().getData();
    List notData = new ArrayList(parser.getAllData());
    notData.removeAll(data);
    TBooleanData d = (TBooleanData)Class.forName(returnClassName).newInstance();
    d.setParser(parser);
    d.setData(notData);
    return d;
  }
  public String getSymbol() {
    return SYMBOL;
  }
}
