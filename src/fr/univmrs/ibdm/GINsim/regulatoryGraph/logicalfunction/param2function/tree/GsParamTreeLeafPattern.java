package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.param2function.tree;

import java.util.Vector;
import java.util.Hashtable;
import java.util.Enumeration;

public class GsParamTreeLeafPattern extends GsParamTreeLeaf {
  private String name = "";
  private Hashtable functions;

  public GsParamTreeLeafPattern() {
    super(null, -1);
    functions = new Hashtable();
  }
  public void print(int depth) {
    for (int i = 0; i < (2 * depth); i++) System.out.print(" ");
    System.out.println(name);
  }
  public String toString() {
    return name;
  }
  public void setValue(Object v) {
  }
  public boolean equals(Object e2) {
    if (e2 instanceof GsParamTreeLeafPattern)
      return ((GsParamTreeLeafPattern)e2).toString().equals(name);
    return false;
  }
  public void makeFunctions(Hashtable h, String f, int dv, boolean pattern) {
    Vector v;
    if (h.containsKey(this)) {
      v = (Vector)h.get(this);
      v.addElement(f);
    }
    else {
      v = new Vector();
      v.addElement(f);
      h.put(this, v);
    }
  }
  public void buildFunctions(GsParamTreeNode node, int dv) {
    Hashtable h = new Hashtable(), h2;
    Object key, key2;
    Vector v;
    String f = "", f2, f3;
    Enumeration enu_values, enu_functions, enu2_values;

    functions.clear();
    node.makeFunctions(h, f, dv, true);
    enu_values = h.keys();
    while (enu_values.hasMoreElements()) {
      key = enu_values.nextElement();
      v = (Vector)h.get(key);
      enu_functions = v.elements();
      f = enu_functions.nextElement().toString();
      if (f.split(" ").length > 1) f = "(" + f + ")";
      while (enu_functions.hasMoreElements()) {
        f2 = enu_functions.nextElement().toString();
        if (f2.split(" ").length > 1) f2 = "(" + f2 + ")";
        f = f + " | " + f2;
      }
      if (key instanceof Integer) {
    	if (functions.containsKey(key)) {
    	  f3 = (String)functions.get(key);
    	  if (f3.split(" ").length > 1) f3 = "(" + f3 + ")";
    	  if (f.split(" ").length > 1) f = "(" + f + ")";
    	  functions.put(key, f3 + " | " + f);
    	}
    	else
    	  functions.put(key, new String(f));
      }
      else {
    	h2 = ((GsParamTreeLeafPattern)key).getFunctions();
    	enu2_values = h2.keys();
    	while (enu2_values.hasMoreElements()) {
    	  key2 = enu2_values.nextElement();
    	  f3 = (String)h2.get(key2);
    	  if (f3.split(" ").length > 1) f3 = "(" + f3 + ")";
    	  if (f.split(" ").length > 1) f = "(" + f + ")";
    	  f2 = f + " & " + f3;
    	  if (functions.containsKey(key2)) {
    	    f3 = (String)functions.get(key2);
    	    if (f3.split(" ").length > 1) f3 = "(" + f3 + ")";
    	    if (f2.split(" ").length > 1) f2 = "(" + f2 + ")";
            functions.put(key2, f3 + " | " + f2);
    	  }
    	  else {
    		functions.put(key2, f2);
    	  }
    	}
      }
    }
  }
  public void setName(String n) {
    name = n;
  }
  public int hashCode() {
    return toString().hashCode();
  }
  public Hashtable getFunctions() {
    return functions;
  }
}
