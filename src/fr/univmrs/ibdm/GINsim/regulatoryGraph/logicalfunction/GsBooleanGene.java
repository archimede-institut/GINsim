package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction;

import fr.univmrs.ibdm.GINsim.global.GsException;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryEdge;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser.TBooleanData;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser.TBooleanOperand;

public class GsBooleanGene extends TBooleanOperand {
  private GsLogicalFunctionList il;
  private GsRegulatoryEdge edge;

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
  public String getVal() {
	  if (edge == null) {
		  return "nil";
	  }
	  return edge.getShortInfo("#");
  }
  public void setInteractionName(GsBooleanParser parser, String value) throws GsException {
	  setParser(parser);
	  setValue(value);
	  this.edge = parser.getEdge(value);
  }
}
