package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser;

import java.util.Vector;

public class TOrOperator extends TBinaryOperator {
  public static final int priority = 0;

  public TOrOperator() {
    super();
  }
  public TBooleanData getValue() throws Exception {
    Vector leftData = ((TBooleanData)leftArg.getValue()).getData();
    Vector rightData = ((TBooleanData)rightArg.getValue()).getData();
    Vector orData = new Vector(leftData);
    orData.addAll(rightData);
    TBooleanData data = (TBooleanData)Class.forName(returnClassName).newInstance();
    data.setData(orData);
    return data;
  }
}
