package org.ginsim.core.graph.regulatorygraph.logicalfunction.param2function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Vector;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.LogicalParameter;


public class FunctionsCreator {
  private RegulatoryGraph graph;
  private Collection<LogicalParameter> interactions;
  private RegulatoryNode currentNode;

  public FunctionsCreator(RegulatoryGraph graph, Collection<LogicalParameter> interactions, RegulatoryNode currentNode) {
    this.graph = graph;
    this.interactions = interactions;
    this.currentNode = currentNode;
  }
  public RegulatoryGraph getGraph() {
    return graph;
  }
  public RegulatoryNode getCurrentNode() {
    return currentNode;
  }
  public ParamTree makeTree(int def) {
	  
	Collection<RegulatoryMultiEdge> l = graph.getIncomingEdges(currentNode);
    HashMap<RegulatoryNode,Object> h = new HashMap();

    for (RegulatoryMultiEdge me: l) {
      h.put(me.getSource(), new Integer(0));
    }
    if (interactions != null) {
      int I;
      for (LogicalParameter p: interactions) {
        for (int j = 0; j < p.EdgeCount(); j++) {
          I = ((Integer) h.get(p.getEdge(j).me.getSource())).intValue() + 1;
          h.put(p.getEdge(j).me.getSource(), new Integer(I));
        }
      }
    }
    ArrayList<Entry<RegulatoryNode, Object>> as = new ArrayList(h.entrySet());
    Collections.sort(as, new Comparator() {
      public int compare(Object o1, Object o2) {
        Entry e1 = (Entry) o1;
        Entry e2 = (Entry) o2;
        Integer first = (Integer) e1.getValue();
        Integer second = (Integer) e2.getValue();
        if (first.compareTo(second) != 0) {
          return first.compareTo(second);
        }
        return ((RegulatoryNode)e1.getKey()).getName().compareTo(((RegulatoryNode) e2.getKey()).getName());
      }
    });
    RegulatoryNode v;
    for (Entry<RegulatoryNode, Object> e: as) {
      v = e.getKey();
      e.setValue(graph.getEdge(v, currentNode));
    }
    return new ParamTree(as, def);
  }
  public Hashtable doIt(boolean comp) {
    Hashtable functions, hash;
    Vector vector;
    String s, s2;
    Object key, key2;

    ParamTree tree = null;
	  if (!comp)
			tree	= makeTree(1234);
		else
			tree = makeTree(0);
    tree.init(interactions, comp);
    tree.process();
    tree.findPatterns();
    functions = tree.getFunctions();
    hash = new Hashtable();
    for (Enumeration enu = functions.keys(); enu.hasMoreElements(); ) {
      s = "";
      key = enu.nextElement();
      Enumeration enu2 = ((Vector)functions.get(key)).elements();
      if (key instanceof ParamTreeLeafPattern) {
      	s = enu2.nextElement().toString();
        if (s.split(" ").length > 1) {
          s = "(" + s + ")";
        }
        while (enu2.hasMoreElements()) {
          s2 = enu2.nextElement().toString();
          if (s2.split(" ").length > 1) {
            s2 = "(" + s2 + ")";
          }
          s = s + " | " + s2;
        }
        enu2 = ((ParamTreeLeafPattern)key).getFunctions().keys();
        while (enu2.hasMoreElements()) {
          key2 = enu2.nextElement();
          s2 = ((ParamTreeLeafPattern)key).getFunctions().get(key2).toString();
          if (!hash.containsKey(key2)) {
            hash.put(key2, new Vector());
          }
          vector = (Vector)hash.get(key2);
          if (!s.equals("")) {
            vector.addElement("(" + s + ") & (" + s2 + ")");
          }
          else {
            vector.addElement(s2);
          }
        }
      }
      else {
        if (!hash.containsKey(key)) {
          hash.put(key, new Vector());
        }
        vector = (Vector)hash.get(key);
        while (enu2.hasMoreElements()) {
          s = (String)enu2.nextElement();
          vector.addElement(s);
        }
      }
    }
    return hash;
  }
  public String makeDNFExpression(int value) {
    String s = "";
    ParamTree tree = makeTree(1234);
    tree.init(interactions, false);
    tree.process();
    s = tree.getDNFForm(value, null);
    return s;
  }
}
