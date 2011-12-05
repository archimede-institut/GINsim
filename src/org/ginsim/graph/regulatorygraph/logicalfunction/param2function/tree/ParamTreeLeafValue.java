package org.ginsim.graph.regulatorygraph.logicalfunction.param2function.tree;

import java.util.Hashtable;
import java.util.Vector;

import org.ginsim.utils.log.LogManager;


public class ParamTreeLeafValue extends ParamTreeLeaf {
  private int value;

  public ParamTreeLeafValue(ParamTreeNode p, int pi) {
    super(p, pi);
    value = 0;
  }
  
  public void print(int depth) {
	  String result = "";
	  for (int i = 0; i < (2 * depth); i++){
		  result += " ";
	  }
	  LogManager.trace( result + value, false);
  }
  
  public void setValue(Object v) {
    value = ((Integer)v).intValue();
  }
  public String toString() {
    return String.valueOf(value);
  }
  public boolean equals(Object e2) {
    if (e2 instanceof ParamTreeLeafValue)
      return (value == ((ParamTreeLeafValue)e2).value);
    return false;
  }
  public void makeFunctions(Hashtable h, String f, int dv, boolean pattern) {
    Vector v;

    if (value != dv) {
      if (h.containsKey(new Integer(value))) {
        v = (Vector)h.get(new Integer(value));
        v.addElement(f);
      }
      else if (!f.equals("")) {
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

  public void makeDNF(Vector v, String s, int value) {
    if (this.value == value) v.addElement(s);
  }
}
