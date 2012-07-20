package org.ginsim.core.graph.regulatorygraph.logicalfunction;

import org.ginsim.common.application.GsException;
import org.ginsim.core.graph.regulatorygraph.RegulatoryEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.parser.TBooleanData;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.parser.TBooleanOperand;


public class BooleanGene extends TBooleanOperand {
  private LogicalFunctionList il;
  private RegulatoryMultiEdge me;
  private RegulatoryEdge edge;

  public BooleanGene() {
    super();
    il = null;
  }
  public TBooleanData getValue() {
    return il;
  }
  public void setLogicalFunctionList(LogicalFunctionList list) {
    il = list;
  }
  public String toString(boolean par) {
    return getVal();
  }
  public String getSaveVal(){
    return ((BooleanParser)parser).getSaveString(value);
  }
  public String getVal() {
	  if (me == null) {
		  return "nil";
	  }
	  if (edge != null) {
		  return edge.getShortInfo();
	  }
	  return me.getSource().getId();
  }
  public void setInteractionName(BooleanParser parser, String value) throws GsException {
	  setParser(parser);
	  setValue(value);
	  Object o = parser.getEdge(value);
	  if (o instanceof RegulatoryMultiEdge) {
		  me = (RegulatoryMultiEdge)o;
	  } else {
		  edge = (RegulatoryEdge)o;
		  me = edge.me;
	  }
  }
  public boolean hasEdge(LogicalFunctionListElement element) {
	  RegulatoryMultiEdge me = element.getEdge();
	  if (me == null) {
		  return false;
	  }
	  if (edge == null) {
		  return this.me == me;
	  }
	  return edge == me.getEdge(element.getIndex());
  }
}
