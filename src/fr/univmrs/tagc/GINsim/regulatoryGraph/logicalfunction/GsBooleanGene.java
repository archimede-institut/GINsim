package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.parser.TBooleanData;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.parser.TBooleanOperand;
import fr.univmrs.tagc.common.GsException;

public class GsBooleanGene extends TBooleanOperand {
  private GsLogicalFunctionList il;
  private GsRegulatoryMultiEdge me;
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
	  if (me == null) {
		  return "nil";
	  }
	  if (edge != null) {
		  return edge.getShortInfo("#");
	  }
	  return me.getSource().getId();
  }
  public void setInteractionName(GsBooleanParser parser, String value) throws GsException {
	  setParser(parser);
	  setValue(value);
	  Object o = parser.getEdge(value);
	  if (o instanceof GsRegulatoryMultiEdge) {
		  me = (GsRegulatoryMultiEdge)o;
	  } else {
		  edge = (GsRegulatoryEdge)o;
		  me = edge.me;
	  }
  }
  public boolean hasEdge(GsLogicalFunctionListElement element) {
	  GsRegulatoryMultiEdge me = element.getEdge();
	  if (me == null) {
		  return false;
	  }
	  if (edge == null) {
		  return this.me == me;
	  }
	  return edge == me.getEdge(element.getIndex());
  }
}
