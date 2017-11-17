package org.ginsim.core.graph.regulatorygraph.logicalfunction.param2function;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.ginsim.common.application.LogManager;


public class ParamTreeLeafPattern extends ParamTreeLeaf {
  private String name = "";
  private Hashtable functions;

  public ParamTreeLeafPattern() {
    super(null, -1);
    functions = new Hashtable();
  }
  public void print(int depth) {
	  String result = "";
	  for (int i = 0; i < (2 * depth); i++){
		  result += " ";
	  }
	  LogManager.trace( result + name, false);
  }
  public String toString() {
    return name;
  }
  public void setValue(Object v) {
  }
  public boolean equals(Object e2) {
    if (e2 instanceof ParamTreeLeafPattern)
      return ((ParamTreeLeafPattern)e2).toString().equals(name);
    return false;
  }
  public void makeFunctions(Hashtable h, String f, int dv, boolean pattern) {
    List v;
    if (h.containsKey(this)) {
      v = (List)h.get(this);
      v.add(f);
    }
    else if (!f.equals("")) {
      v = new ArrayList();
      v.add(f);
      h.put(this, v);
    }
  }
  public void buildFunctions(ParamTreeNode node, int dv) {
    Hashtable h = new Hashtable(), h2;
    Object key, key2;
    List v;
    String f = "", f2, f3;
    Iterator enu_values, enu_functions, enu2_values;

    functions.clear();
    node.makeFunctions(h, f, dv, true);
    enu_values = h.keySet().iterator();
    while (enu_values.hasNext()) {
      key = enu_values.next();
      v = (List)h.get(key);
      enu_functions = v.iterator();
      f = enu_functions.next().toString();
      if (f.split(" ").length > 1) f = "(" + f + ")";
      while (enu_functions.hasNext()) {
        f2 = enu_functions.next().toString();
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
    	h2 = ((ParamTreeLeafPattern)key).getFunctions();
    	enu2_values = h2.keySet().iterator();
    	while (enu2_values.hasNext()) {
    	  key2 = enu2_values.next();
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

  public void makeDNF(List v, String s, int value) {
  }
}
