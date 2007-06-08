package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.param2function;

import java.util.*;
import java.util.Map.Entry;

import fr.univmrs.ibdm.GINsim.data.GsDirectedEdge;
import fr.univmrs.ibdm.GINsim.graph.GsGraphManager;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.*;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.param2function.tree.GsParamTree;

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

    while (it.hasNext()) {
      de = (GsDirectedEdge)it.next();
      h.put(de.getSourceVertex(), new Integer(0));
      //System.err.println(de.getSourceVertex());
    }
    int I;
    for (int i = 0; i < interactions.size(); i++) {
      GsLogicalParameter p = (GsLogicalParameter)interactions.elementAt(i);
      //System.err.println(p + " : " + p.getValue());
      for (int j = 0; j < p.EdgeCount(); j++) {
        I = ((Integer)h.get(p.getEdge(j).data.getSource())).intValue() + 1;
        h.put(p.getEdge(j).data.getSource(), new Integer(I));
        //System.err.println("  " + p.getEdge(j).data.getSource().getId() + " , " + p.getEdge(j).index);
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
    tree = new GsParamTree(as);
    tree.init(interactions);
    tree.print();
    /*Vector nodeOrder = graph.getNodeOrder();

        Vector allowedEdges = new Vector();
        for (int i = 0 ; i < nodeOrder.size() ; i++) {
          GsDirectedEdge o = (GsDirectedEdge)manager.getEdge(nodeOrder.get(i), currentVertex);
          if (o != null) allowedEdges.addElement(o);
        }
        Iterator it = allowedEdges.iterator();
        Hashtable v2 = new Hashtable();
        Hashtable v3 = new Hashtable();
        int N = 0;
        while (it.hasNext()) {
          GsDirectedEdge e = (GsDirectedEdge)it.next();
          GsRegulatoryMultiEdge me = (GsRegulatoryMultiEdge)e.getUserObject();
          for (int i = 0; i < me.getEdgeCount(); i++) {
            v2.put(me.getId(i), new Integer(N));
            v3.put(new Integer(N++), me.getId(i));
          }
        }

        Vector pattern_1 = new Vector();
        Vector inter = ((GsTableInteractionsModel)jTable.getModel()).getInteractions();
        for (int i = 0; i < inter.size(); i++) {
          GsLogicalParameter p = (GsLogicalParameter)inter.elementAt(i);
          N = 0;
          for (int k = 0; k < p.EdgeCount(); k++)
            N += 1 << ((Integer) v2.get(p.getEdge(k).data.getId(p.getEdge(k).index))).intValue();
          pattern_1.addElement(new Integer(N));
        }
        if (currentVertex.getBaseValue() == 1) pattern_1.addElement(new Integer(0));
        Vector pattern_0 = new Vector();
        for (int i = 0; i < Math.pow(2, v2.size()); i++)
          if (!pattern_1.contains(new Integer(i))) {
            pattern_0.addElement(new Integer(i));

          }
        int mask = 0, value = 0, n = 0, h, k = 0;
        String s;
        for (int y = 0; y < 3; y++) {
        //while (!pattern_1.isEmpty()) {
          for (int i = 1; i <= v2.size(); i++) {
            for (int j = 0; j < Math.pow(2, i); j++) {
              for (k = 0; k <= (v2.size() - i); k++) {
                n = 0;
                mask = ((int)Math.pow(2, i) - 1) << k;
                value = j << k;
                for (int i_0 = 0; i_0 < pattern_0.size(); i_0++) {
                  h = (~((((Integer)pattern_0.get(i_0)).intValue() & mask) ^ value) & mask);
                  if (h == mask) n++;
                }
                if (n == pattern_0.size()) {
                  System.err.println("nb bits = " + i + "   value = " + j + "   pos = " + k + "   N = " + n);
                  System.err.println("mask = " + mask);
                  break;
                }
              }
              if (n == pattern_0.size()) {
                s = "";
                for (int m = 0; m < i; m++) {
                  s = (String)v3.get(new Integer(k + m));
                  s = s.substring(0, s.lastIndexOf("_")) + "#" + s.substring(s.lastIndexOf("_") + 1);
                  if ((value & (int)Math.pow(2, m + k)) == value)
                    s = "!" + s;
                  if (m < (i - 1)) s += " & ";
                }
                System.err.println("---> " + s);
                it = pattern_1.iterator();
                while (it.hasNext()) {
                  Integer p = (Integer)it.next();
                  int v = (~(value & mask) & mask);
                  h = (~((p.intValue() & mask) ^ v) & mask);
                  if (h == mask) {
                    System.err.println("A virer : " + p);
                    pattern_1.remove(p);
                    it = pattern_1.iterator();
                  }
                }
              }
            }
          }
          System.err.println(pattern_1);
          //break;
        }
        System.err.println(pattern_0);
        System.err.println(v2);*/

  }
}
