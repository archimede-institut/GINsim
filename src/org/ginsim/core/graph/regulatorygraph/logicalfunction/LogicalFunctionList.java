package org.ginsim.core.graph.regulatorygraph.logicalfunction;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.ginsim.core.graph.regulatorygraph.logicalfunction.parser.TBooleanData;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.parser.TBooleanParser;


public class LogicalFunctionList implements TBooleanData {
  private List logicalFunctions;
  private BooleanParser parser;

  public LogicalFunctionList() {
    super();
    parser = null;
  }
  public void setParser(TBooleanParser p) {
    parser = (BooleanParser)p;
  }
  public List getData() {
    return logicalFunctions;
  }
  public void setData(List v) {
    logicalFunctions = v;
  }
  public Vector getAsStringVector() {
    List params = parser.getParams(logicalFunctions);
    Iterator it = params.iterator(), it2;
    Vector v, v2 = new Vector();
    Integer element;
    String s;

    while (it.hasNext()) {
      v = (Vector)it.next();
      it2 = v.iterator();
      s = "";
      while (it2.hasNext()) {
        element = (Integer)it2.next();
        s = s + element.toString() + " ";
      }
      v2.addElement(s.trim());
    }
    return v2;
  }

//  public void print() {
//    Iterator it = getAsStringVector().iterator();
//    while (it.hasNext()) {
//		System.err.println(it.next());
//	}
//  }
}
