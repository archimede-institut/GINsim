package org.ginsim.core.graph.regulatorygraph.logicalfunction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
  public List getAsStringList() {
    List params = parser.getParams(logicalFunctions);
    Iterator it = params.iterator(), it2;
    List v, v2 = new ArrayList();
    Integer element;
    String s;

    while (it.hasNext()) {
      v = (List)it.next();
      it2 = v.iterator();
      s = "";
      while (it2.hasNext()) {
        element = (Integer)it2.next();
        s = s + element.toString() + " ";
      }
      v2.add(s.trim());
    }
    return v2;
  }

}
