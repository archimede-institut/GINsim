package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser.TBooleanOperand;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser.TBooleanData;

public class GsBooleanGene extends TBooleanOperand {
  private GsLogicalFunctionList il;

  public GsBooleanGene() {
    super();
    il = null;
  }
  public TBooleanData getValue() {
    return il;
  }
  public void setLogicalFunctionList(GsLogicalFunctionList list) {
    il = list;
  }
  public String toString() {
    return getVal();
  }
  public String getSaveVal(){
    return ((GsBooleanParser)parser).getSaveString(value);
  }
}
