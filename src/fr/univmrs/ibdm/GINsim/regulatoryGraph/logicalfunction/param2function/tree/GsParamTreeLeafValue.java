package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.param2function.tree;

import java.util.Vector;
import java.util.Hashtable;

public class GsParamTreeLeafValue extends GsParamTreeLeaf {
  private int value;

  public GsParamTreeLeafValue(GsParamTreeNode p, int pi) {
    super(p, pi);
    value = 0;
  }
  public void print(int depth) {
    for (int i = 0; i < (2 * depth); i++) System.out.print(" ");
    System.out.println(value);
  }
  public void setValue(Object v) {
    value = ((Integer)v).intValue();
  }
  public String toString() {
    return String.valueOf(value);
  }
  public boolean equals(Object e2) {
    if (e2 instanceof GsParamTreeLeafValue)
      return (value == ((GsParamTreeLeafValue)e2).value);
    return false;
  }
  public void makeFunctions(Hashtable h, String f, int dv, boolean pattern) {
    Vector v;

    if (value != dv) {
      if (h.containsKey(new Integer(value))) {
        v = (Vector)h.get(new Integer(value));
        v.addElement(f);
      }
      else {
        v = new Vector();
        v.addElement(f);
        h.put(new Integer(value), v);
      }
    }
  }
  public void buildFunctions(Hashtable h, String f, int dv) {
    makeFunctions(h, f, dv, false);
  }
  public int hashCode() {
    return toString().hashCode();
  }
}
