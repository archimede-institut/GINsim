package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction;

import java.util.*;

import fr.univmrs.ibdm.GINsim.data.GsDirectedEdge;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryEdge;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser.TBooleanParser;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser.TBooleanTreeNode;
import fr.univmrs.tagc.global.GsException;

public class GsBooleanParser extends TBooleanParser {
  private Hashtable operandList;
  private static String returnClassName = "fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.GsLogicalFunctionList";
  private static String operandClassName = "fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.GsBooleanGene";
  private Object[] allParams;
  private GsRegulatoryGraph graph;
  private GsRegulatoryVertex vertex;

  public GsBooleanParser(List edgesList) throws ClassNotFoundException {
    super(returnClassName, operandClassName);
    nodeFactory = new GsBooleanTreeNodeFactory(returnClassName, operandClassName, this);
    makeOperandList(edgesList);
    setAllData(edgesList);
  }
  public boolean verifOperandList(List list) {
	  // FIXME: revert or delete dead code
		return true;
//    List v = new ArrayList();
//    Object o;
//    Iterator it = operandList.keySet().iterator();
//    GsRegulatoryEdge re;
//    GsDirectedEdge e;
//    GsRegulatoryVertex source;
//
//    while (it.hasNext()) {
//      o = it.next();
//      if (o instanceof GsRegulatoryEdge) {
//        re = (GsRegulatoryEdge) o;
//        source = re.me.getSource();
//        v.add(source.getId());
//        for (int i = 0; i < re.me.getEdgeCount(); i++) {
//			v.add(re.me.getEdge(i).getShortInfo("#"));
//		}
//      }
//      else if (o instanceof GsDirectedEdge) {
//        e = (GsDirectedEdge) o;
//        source = (GsRegulatoryVertex)e.getSourceVertex();
//        v.add(source.getId() + "#1");
//      }
//      else if (o instanceof GsRegulatoryVertex) {
//        source = (GsRegulatoryVertex)o;
//        v.add(source.getId());
//      }
//    }
//    return v.containsAll(list);
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
      for (int k = 0; k < n; k++) {
		F[i].add(new GsLogicalFunctionListElement((GsRegulatoryMultiEdge)e.getUserObject(), k));
	}
      N[i] = n;
      K[i] = 0;
      p *= n + 1;
      i++;
    }
    K[edgesList.size() - 1] = -1;
    for (i = 1; i <= p; i++) {
      for (j = edgesList.size() - 1; j >= 0; j--) {
        K[j]++;
        if (K[j] > N[j]) {
			K[j] = 0;
		} else {
			break;
		}
      }
      if (j >= 0) {
        L = new Vector();
        for (j = 0; j < edgesList.size(); j++) {
			if (!((GsLogicalFunctionListElement) F[j].get(K[j])).toString().equals("")) {
				L.addElement(F[j].get(K[j]));
			}
		}
        v.addElement(L);
      } else {
		break;
	}
    }
    allParams = v.toArray();
    allData = new Vector(allParams.length);
    for (i = 0; i < allParams.length; i++) {
      allData.addElement(new Integer(i));
    }
  }

  public Vector getParams(List indexes) {
    Vector v = new Vector();
    for (Iterator it = indexes.iterator(); it.hasNext(); ) {
		v.addElement(allParams[((Integer)it.next()).intValue()]);
	}
    return v;
  }
  public Object[] getAllParams() {
    return allParams;
  }
  private void makeOperandList(List edgesList) {
    Iterator it = edgesList.iterator();
    GsDirectedEdge e;
    GsRegulatoryVertex source;
    GsRegulatoryEdge re;
    GsRegulatoryMultiEdge me;

    operandList = new Hashtable();
    while (it.hasNext()) {
      e = (GsDirectedEdge)it.next();
      me = (GsRegulatoryMultiEdge)e.getUserObject();
      source = (GsRegulatoryVertex)e.getSourceVertex();
      operandList.put(source, source.getId());
      for (int i = 0; i < me.getEdgeCount(); i++) {
        re = me.getEdge(i);
        if (me.getEdgeCount() > 1) {
			operandList.put(re/*.getShortInfo("#")*/, re.getShortDetail("_"));
		} else {
			operandList.put(e/*.getId() + "#" + (i + 1)*/, re.getShortDetail("_"));
		}
      }
    }
  }
  public String getSaveString(String s) {
    return (String)operandList.get(s);
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
  
  public boolean compile(String v, GsRegulatoryGraph graph, GsRegulatoryVertex vertex) {
	  this.graph = graph;
	  this.vertex = vertex;
	  try {
		  return super.compile(v);
	  } catch (Exception e) {
		  if (e instanceof GsException) {
			  System.out.println("working");
		  }
		  e.printStackTrace();
		  return false;
	  }
  }
  public Object getEdge(String value) throws GsException {
	  String nodeID;
	  int edgeTh = value.indexOf(":");
	  int edgeNumber = value.indexOf("#");
	  if (edgeTh != -1) {
		  int i = value.lastIndexOf(":");
		  if (edgeTh != i) {
			  throw new GsException(GsException.GRAVITY_ERROR, "invalid identifier: "+value);
		  }
		  nodeID = value.substring(0, i);
		  try {
			  edgeTh = Integer.parseInt(value.substring(i+1));
		  } catch (Exception e) {
			  throw new GsException(GsException.GRAVITY_ERROR, "invalid edge threshold in "+value);
		  }
	  } else if (edgeNumber != -1) {
		  int i = value.lastIndexOf("#");
		  if (edgeNumber != i) {
			  throw new GsException(GsException.GRAVITY_ERROR, "invalid identifier: "+value);
		  }
		  nodeID = value.substring(0, i);
		  try {
			  edgeNumber = Integer.parseInt(value.substring(i+1));
		  } catch (Exception e) {
			  throw new GsException(GsException.GRAVITY_ERROR, "invalid edge number in "+value);
		  }
	  } else {
		  nodeID = value;
	  }
	  Iterator it = graph.getNodeOrder().iterator();
	  GsRegulatoryVertex vertex = null;
	  boolean found = false;
	  while (it.hasNext()) {
		  vertex = (GsRegulatoryVertex)it.next();
		  if (vertex.getId().equals(nodeID)) {
			  found = true;
			  break;
		  }
	  }
	  if (!found) {
		  throw new GsException(GsException.GRAVITY_NORMAL, "no such node");
	  }
	  GsDirectedEdge de = (GsDirectedEdge)graph.getGraphManager().getEdge(vertex, this.vertex);
	  GsRegulatoryMultiEdge me = (GsRegulatoryMultiEdge)de.getUserObject();
	  if (edgeTh != -1) {
		  GsRegulatoryEdge edge = me.getEdgeForThreshold(edgeTh);
		  if (edge == null) {
			  throw new GsException(GsException.GRAVITY_NORMAL, "no such edge threshold");
		  }
		  return edge;
	  }
	  if (edgeNumber != -1) {
		return me.getEdge(edgeNumber-1);  
	  }
	  return me;
  }
}
