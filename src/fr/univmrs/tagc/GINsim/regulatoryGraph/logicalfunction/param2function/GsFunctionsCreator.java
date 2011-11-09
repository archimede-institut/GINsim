package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.param2function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map.Entry;
import java.util.Vector;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsLogicalParameter;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.param2function.tree.GsParamTree;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.param2function.tree.GsParamTreeLeafPattern;

public class GsFunctionsCreator {
  private GsRegulatoryGraph graph;
  private Collection<GsLogicalParameter> interactions;
  private GsRegulatoryVertex currentVertex;

  public GsFunctionsCreator(GsRegulatoryGraph graph, Collection<GsLogicalParameter> interactions, GsRegulatoryVertex currentVertex) {
    this.graph = graph;
    this.interactions = interactions;
    this.currentVertex = currentVertex;
  }
  public GsRegulatoryGraph getGraph() {
    return graph;
  }
  public GsRegulatoryVertex getCurrentVertex() {
    return currentVertex;
  }
  public GsParamTree makeTree(int def) {
	  
	Collection<GsRegulatoryMultiEdge> l = graph.getIncomingEdges(currentVertex);
    HashMap<GsRegulatoryVertex,Object> h = new HashMap();

    for (GsRegulatoryMultiEdge me: l) {
      h.put(me.getSource(), new Integer(0));
    }
    if (interactions != null) {
      int I;
      for (GsLogicalParameter p: interactions) {
        for (int j = 0; j < p.EdgeCount(); j++) {
          I = ((Integer) h.get(p.getEdge(j).me.getSource())).intValue() + 1;
          h.put(p.getEdge(j).me.getSource(), new Integer(I));
        }
      }
    }
    ArrayList<Entry<GsRegulatoryVertex, Object>> as = new ArrayList(h.entrySet());
    Collections.sort(as, new Comparator() {
      public int compare(Object o1, Object o2) {
        Entry e1 = (Entry) o1;
        Entry e2 = (Entry) o2;
        Integer first = (Integer) e1.getValue();
        Integer second = (Integer) e2.getValue();
        if (first.compareTo(second) != 0) {
          return first.compareTo(second);
        }
        return ((GsRegulatoryVertex)e1.getKey()).getName().compareTo(((GsRegulatoryVertex) e2.getKey()).getName());
      }
    });
    GsRegulatoryVertex v;
    for (Entry<GsRegulatoryVertex, Object> e: as) {
      v = e.getKey();
      e.setValue(graph.getEdge(v, currentVertex));
    }
    return new GsParamTree(as, def);
  }
  public Hashtable doIt(boolean comp) {
    Hashtable functions, hash;
    Vector vector;
    String s, s2;
    Object key, key2;

    GsParamTree tree = null;
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
      if (key instanceof GsParamTreeLeafPattern) {
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
        enu2 = ((GsParamTreeLeafPattern)key).getFunctions().keys();
        while (enu2.hasMoreElements()) {
          key2 = enu2.nextElement();
          s2 = ((GsParamTreeLeafPattern)key).getFunctions().get(key2).toString();
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
    GsParamTree tree = makeTree(1234);
    tree.init(interactions, false);
    tree.process();
    s = tree.getDNFForm(value, null);
    return s;
  }
}
