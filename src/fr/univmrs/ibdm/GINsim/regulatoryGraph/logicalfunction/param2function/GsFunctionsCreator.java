package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.param2function;

import java.util.*;
import java.util.Map.Entry;

import fr.univmrs.ibdm.GINsim.data.*;
import fr.univmrs.ibdm.GINsim.graph.*;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.*;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.param2function.tree.*;

public class GsFunctionsCreator {
  private GsGraphManager graphManager;
  private Vector interactions;
  private GsRegulatoryVertex currentVertex;

  public GsFunctionsCreator(GsGraphManager graphManager, Vector interactions, GsRegulatoryVertex currentVertex) {
    super();
    this.graphManager = graphManager;
    this.interactions = interactions;
    this.currentVertex = currentVertex;
  }
  public Hashtable doIt(boolean fixBasalValue) {
    List l = graphManager.getIncomingEdges(currentVertex);
    Iterator it = l.iterator();
    GsDirectedEdge de;
    HashMap h = new HashMap();
    Hashtable functions, hash;
    Vector vector;
    String s, s2;

    while (it.hasNext()) {
      de = (GsDirectedEdge)it.next();
      h.put(de.getSourceVertex(), new Integer(0));
    }
    int I;
    for (int i = 0; i < interactions.size(); i++) {
      GsLogicalParameter p = (GsLogicalParameter)interactions.elementAt(i);
      for (int j = 0; j < p.EdgeCount(); j++) {
        I = ((Integer)h.get(p.getEdge(j).data.getSource())).intValue() + 1;
        h.put(p.getEdge(j).data.getSource(), new Integer(I));
      }
    }
    Object key, key2;
    ArrayList as = new ArrayList(h.entrySet());
    Collections.sort(as, new Comparator() {
      public int compare(Object o1, Object o2) {
        Entry e1 = (Entry)o1 ;
        Entry e2 = (Entry)o2 ;
        Integer first = (Integer)e1.getValue();
        Integer second = (Integer)e2.getValue();
        if (first.compareTo(second) != 0)
          return first.compareTo(second);
        return ((GsRegulatoryVertex)e1.getKey()).getName().compareTo(((GsRegulatoryVertex)e2.getKey()).getName());
      }
    });
    it = as.iterator();
    Entry e;
    GsRegulatoryVertex v;
    GsParamTree tree;
    while (it.hasNext()) {
      e = (Entry)it.next();
      v = (GsRegulatoryVertex)e.getKey();
      e.setValue(((GsDirectedEdge)graphManager.getEdge(v, currentVertex)).getUserObject());
    }
    tree = new GsParamTree(as, 1234);
    if (fixBasalValue) tree.setBasalValue((int)currentVertex.getBaseValue());
    tree.init(interactions);
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
        if (s.split(" ").length > 1) s = "(" + s + ")";
        while (enu2.hasMoreElements()) {
          s2 = enu2.nextElement().toString();
          if (s2.split(" ").length > 1) s2 = "(" + s2 + ")";
          s = s + " | " + s2;
        }
        enu2 = ((GsParamTreeLeafPattern)key).getFunctions().keys();
        while (enu2.hasMoreElements()) {
          key2 = enu2.nextElement();
          s2 = ((GsParamTreeLeafPattern)key).getFunctions().get(key2).toString();
          if (!hash.containsKey((Integer)key2)) hash.put((Integer)key2, new Vector());
          vector = (Vector)hash.get((Integer)key2);
          if (!s.equals(""))
        	vector.addElement("(" + s + ") & (" + s2 + ")");
          else
        	vector.addElement(s2);
        }
      }
      else {
    	  if (!hash.containsKey((Integer)key)) hash.put((Integer)key, new Vector());
          vector = (Vector)hash.get((Integer)key);
    	  while (enu2.hasMoreElements()) {
    		s = (String)enu2.nextElement();
    	    vector.addElement(s);
          }
      }
    }
    return hash;
  }
}