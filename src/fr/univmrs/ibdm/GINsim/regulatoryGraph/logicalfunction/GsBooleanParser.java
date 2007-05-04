package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import fr.univmrs.ibdm.GINsim.data.GsDirectedEdge;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser.TBooleanParser;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser.TBooleanTreeNode;

public class GsBooleanParser extends TBooleanParser {
  private Vector operandList;
  private static String returnClassName = "fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.GsLogicalFunctionList";
  private static String operandClassName = "fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.GsBooleanGene";
  private Object[] allParams;

  public GsBooleanParser(List edgesList) throws ClassNotFoundException {
    super(returnClassName, operandClassName);
    nodeFactory = new GsBooleanTreeNodeFactory(returnClassName, operandClassName, this);
    makeOperandList(edgesList);
    setAllData(edgesList);
  }
  public boolean verifOperandList(List list) {
    return (operandList.containsAll(list));
  }
  protected void setAllData(List edgesList) {
    Iterator it = edgesList.iterator();
    GsDirectedEdge e;

    ArrayList[] F = new ArrayList[operandList.size()];
    int[] N = new int[operandList.size()];
    int[] K = new int[operandList.size()];
    int n, i, p, j;

    Vector L, v;

    v = new Vector();

    i = 0;
    p = 1;
    while (it.hasNext()) {
      e = (GsDirectedEdge)it.next();
      n = ((GsRegulatoryMultiEdge)e.getUserObject()).getEdgeCount();
      F[i] = new ArrayList(n + 1);
      F[i].add(new GsLogicalFunctionListElement(null, -1));
      for (int k = 0; k < n; k++)
        F[i].add(new GsLogicalFunctionListElement((GsRegulatoryMultiEdge)e.getUserObject(), k));
      N[i] = n;
      K[i] = 0;
      p *= n + 1;
      i++;
    }
    K[edgesList.size() - 1] = -1;
    for (i = 1; i <= p; i++) {
      for (j = edgesList.size() - 1; j >= 0; j--) {
        K[j]++;
        if (K[j] > N[j])
          K[j] = 0;
        else
          break;
      }
      if (j >= 0) {
        L = new Vector();
        for (j = 0; j < edgesList.size(); j++)
          if (!((GsLogicalFunctionListElement) F[j].get(K[j])).toString().equals(""))
            L.addElement(F[j].get(K[j]));
        //System.err.println(L);

        v.addElement(L);
      }
      else
        break;
    }
    allParams = v.toArray();
    allData = new Vector();
    for (i = 0; i < allParams.length; i++) allData.addElement(new Integer(i));
  }

  public Vector getParams(Vector indexes) {
    Vector v = new Vector();
    for (Iterator it = indexes.iterator(); it.hasNext(); )
      v.addElement(allParams[((Integer)it.next()).intValue()]);
    return v;
  }
  public Object[] getAllParams() {
    return allParams;
  }
  private void makeOperandList(List edgesList) {
    Iterator it = edgesList.iterator();
    GsDirectedEdge e;
    GsRegulatoryVertex source;

    operandList = new Vector();
    while (it.hasNext()) {
      e = (GsDirectedEdge)it.next();
      source = (GsRegulatoryVertex)e.getSourceVertex();
      operandList.addElement(source.getId());
      for (int i = 0; i < ((GsRegulatoryMultiEdge)e.getUserObject()).getEdgeCount(); i++)
        operandList.addElement(source.getId() + "#" + String.valueOf(i));
    }
  }
  public TBooleanTreeNode getRoot() {
    return root;
  }
  public static String getReturnClassName() {
    return returnClassName;
  }
  public static String getOperandClassName() {
    return operandClassName;
  }
  public void setRoot(TBooleanTreeNode root) {
    this.root = root;
  }
}
