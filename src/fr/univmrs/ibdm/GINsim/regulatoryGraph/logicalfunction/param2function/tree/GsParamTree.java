package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.param2function.tree;

import java.util.ArrayList;
import java.util.Iterator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import java.util.Map.Entry;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import java.util.Vector;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsLogicalParameter;

public class GsParamTree {
  private GsParamTreeNode root = null;
  private int depth = -1;

  public GsParamTree(ArrayList l) {
    super();
    Iterator it = l.iterator();
    Entry e;

    while (it.hasNext()) {
      e = (Entry)it.next();
      if (e.getValue() != null)
        addLevel(++depth, (GsRegulatoryVertex)e.getKey(), (GsRegulatoryMultiEdge)e.getValue());
    }
    addLeaves();
  }
  private void addLevel(int level, GsRegulatoryVertex v, GsRegulatoryMultiEdge me) {
    GsParamTreeNode e;
    if (level == 0)
      root = new GsParamTreeNode(v, me);
    else {
      Vector nodes = new Vector();
      getNodes(level - 1, 0, root, nodes);

      for (int i = 0; i < nodes.size(); i++) {
        e = (GsParamTreeNode)nodes.elementAt(i);
        for (int j = 0; j <= e.getNbEdge(); j++) e.addSon(new GsParamTreeNode(v, me), j);
      }
    }
  }
  private void getNodes(int wantedDepth, int currentDepth, GsParamTreeElement node, Vector v) {
    if (currentDepth == wantedDepth)
      v.addElement(node);
    else {
      for (int i = 0; i < node.getNbSons(); i++) {
        if (!node.getSon(i).isLeaf())
          getNodes(wantedDepth, currentDepth + 1, node.getSon(i), v);
      }
    }
  }
  private void addLeaves() {
    GsParamTreeNode e;
    Vector v = new Vector();
    getNodes(depth, 0, root, v);
    for (int i = 0; i < v.size(); i++) {
      e = (GsParamTreeNode)v.elementAt(i);
      for (int j = 0; j <= e.getNbEdge(); j++) e.addSon(new GsParamTreeLeaf(), j);
    }
  }
  public void init(Vector interactions) {
    for (int i = 0; i < interactions.size(); i++)
      init((GsLogicalParameter)interactions.elementAt(i));
  }
  private void init(GsLogicalParameter lp) {
    getLeaf(lp).setValue(lp.getValue());
  }
  private GsParamTreeLeaf getLeaf(GsLogicalParameter lp) {
    for (int i = 0; i <= depth; i++) {

    }
    return null;
  }
  public void print() {
    root.print(0);
  }
}
