package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.parser.TBooleanData;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.parser.TBooleanParser;

public class GsLogicalFunctionList implements TBooleanData {
  private List logicalFunctions;
  private GsBooleanParser parser;

  public GsLogicalFunctionList() {
    super();
    parser = null;
  }
  public void setParser(TBooleanParser p) {
    parser = (GsBooleanParser)p;
  }
  public List getData() {
    return logicalFunctions;
  }
  public void setData(List v) {
    logicalFunctions = v;
  }
  public Vector getAsStringVector() {
    Vector params = parser.getParams(logicalFunctions);
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

  public void print() {
    Iterator it = getAsStringVector().iterator();
    while (it.hasNext()) {
		System.err.println(it.next());
	}
  }
}
