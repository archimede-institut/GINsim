package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.param2function;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.Map.Entry;

import fr.univmrs.ibdm.GINsim.data.GsDirectedEdge;
import fr.univmrs.ibdm.GINsim.graph.GsGraphManager;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsLogicalParameter;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.param2function.tree.GsParamTree;
import java.util.Enumeration;
import java.util.Hashtable;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.param2function.tree.GsParamTreeLeafPattern;

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
  public void doIt() {
    List l = graphManager.getIncomingEdges(currentVertex);
    Iterator it = l.iterator();
    GsDirectedEdge de;
    HashMap h = new HashMap();
    Hashtable functions;
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
    Object key;
    ArrayList as = new ArrayList(h.entrySet());
    Collections.sort(as, new Comparator() {
      public int compare(Object o1, Object o2) {
        Entry e1 = (Entry)o1 ;
        Entry e2 = (Entry)o2 ;
        Integer first = (Integer)e1.getValue();
        Integer second = (Integer)e2.getValue();
        return first.compareTo(second);
      }
    });
    it = as.iterator();
    Entry e;
    GsRegulatoryVertex v;
    GsParamTree tree;
    while (it.hasNext()) {
      e = (Entry)it.next();
      v = (GsRegulatoryVertex)e.getKey();
      if (((Integer)e.getValue()).intValue() == 0)
        e.setValue(null);
      else
        e.setValue(((GsDirectedEdge)graphManager.getEdge(v, currentVertex)).getUserObject());
    }
    tree = new GsParamTree(as, (int)currentVertex.getBaseValue());
    tree.init(interactions);
    tree.process();
    tree.print();
    tree.findPatterns();
    tree.print();
    functions = tree.getFunctions();
    for (Enumeration enu = functions.keys(); enu.hasMoreElements(); ) {
      s = "";
      key = enu.nextElement();
      vector = (Vector)functions.get(key);
      Enumeration enu2 = vector.elements();
      if (key instanceof GsParamTreeLeafPattern) {
        s = enu2.nextElement().toString();
        if (s.split(" ").length > 1) s = "(" + s + ")";
        while (enu2.hasMoreElements()) {
          s2 = enu2.nextElement().toString();
          if (s2.split(" ").length > 1) s2 = "(" + s2 + ")";
          s = s + " | " + s2;
        }
        System.out.println(s + "\t" + ((GsParamTreeLeafPattern)key).toString());
      }
      else
        while (enu2.hasMoreElements()) System.out.println(enu2.nextElement() + "\t" + key);
    }
  }
}
