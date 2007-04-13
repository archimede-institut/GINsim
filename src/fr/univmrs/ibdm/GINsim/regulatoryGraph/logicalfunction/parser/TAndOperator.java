package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser;

import java.util.Vector;
import java.util.Iterator;

public class TAndOperator extends TBinaryOperator {
  public static final int priority = 0;
  protected static String SYMBOL = "&";

  public TAndOperator() {
    super();
  }
  public TBooleanData getValue() throws Exception {
    Vector leftData = ((TBooleanData)leftArg.getValue()).getData();
    Vector rightData = ((TBooleanData)rightArg.getValue()).getData();
    Iterator itLeft = leftData.iterator();
    Object item;
    Vector andData = new Vector();

    while (itLeft.hasNext()) {
      item = itLeft.next();
      if (rightData.contains(item)) andData.addElement(item);
    }

    TBooleanData data = (TBooleanData)Class.forName(returnClassName).newInstance();
    data.setData(andData);
    return data;
  }
  public String getSymbol() {
    return SYMBOL;
  }
}
