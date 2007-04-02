package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction;

import java.util.Vector;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser.TBooleanData;
import java.util.Iterator;

public class GsLogicalFunctionList implements TBooleanData {
  private Vector logicalFunctions;

  public GsLogicalFunctionList() {
    super();
  }
  public Vector getData() {
    return logicalFunctions;
  }
  public void setData(Vector v) {
    logicalFunctions = v;
  }
  public Vector getAsStringVector() {
    Iterator it = logicalFunctions.iterator(), it2;
    Vector v, v2 = new Vector();
    GsLogicalFunctionListElement element;
    String s;

    while (it.hasNext()) {
      v = (Vector)it.next();
      it2 = v.iterator();
      s = "";
      while (it2.hasNext()) {
        element = (GsLogicalFunctionListElement) it2.next();
        s = s + element.toString() + " ";
      }
      v2.addElement(s.trim());
    }
    return v2;
  }
  public void print() {
    Iterator it = getAsStringVector().iterator();
    while (it.hasNext()) System.err.println(it.next());
  }
}
