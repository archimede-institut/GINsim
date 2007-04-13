package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser;

import java.util.Vector;
import java.util.Iterator;

public class TNotOperator extends TUnaryOperator {
  public static final int priority = 1;
  protected static String SYMBOL = "!";

  public TNotOperator() {
    super();
  }
  public TBooleanData getValue() throws Exception {
    Vector allData = parser.getAllData();
    Vector data = ((TBooleanData)arg.getValue()).getData();
    Iterator itAll = allData.iterator();
    Object item;
    Vector notData = new Vector();

    while (itAll.hasNext()) {
      item = itAll.next();
      if (!data.contains(item)) notData.addElement(item);
    }
    TBooleanData d = (TBooleanData)Class.forName(returnClassName).newInstance();
    d.setData(notData);
    return d;
  }
  public String getSymbol() {
    return SYMBOL;
  }
}
