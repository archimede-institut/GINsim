package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import java.awt.Color;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import fr.univmrs.ibdm.GINsim.graph.GsGraphNotificationMessage;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsEdgeIndex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsLogicalParameter;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryMultiEdge;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.GsBooleanParser;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.GsLogicalFunctionList;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.GsLogicalFunctionListElement;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser.TBooleanTreeNode;

public class GsTreeInteractionsModel implements TreeModel {
  //the vector of interaction
  private Vector interactions;

  //the current selected node
  private GsRegulatoryVertex node;
  private GsRegulatoryGraph graph;
  private Vector v_ok;
  private GsTreeString root;

  private Vector treeModelListeners = new Vector();

  public GsTreeInteractionsModel(GsRegulatoryGraph graph) {
    root = new GsTreeString(null, "Function list");
    this.v_ok = new Vector();
    this.interactions = null;
    this.graph = graph;
  }
  public void setNode(GsRegulatoryVertex no) {
    node = no;
    if (node != null)
      this.interactions = node.getV_logicalParameters();
    else
      this.interactions = null;

    v_ok.clear();
    for (int i=0 ; i<interactions.size() ; i++) {
        v_ok.add(Boolean.TRUE);
    }
    v_ok.add(Boolean.TRUE);
  }
  public void removeEdge(GsRegulatoryMultiEdge multiEdge, int index) {
    GsTreeValue val;
    GsTreeExpression exp;

    for (int i = 0; i < root.getChildCount(); i++) {
      val = (GsTreeValue)root.getChild(i);
      for (int j = 0; j < val.getChildCount(); j++) {
        exp = (GsTreeExpression)val.getChild(j);
        if (exp.remove(multiEdge, index) == null) {
          val.removeChild(j);
          j--;
        }
        else {
          updateExpression((short)val.getValue(), exp);
        }
        fireTreeStructureChanged(root);
      }
    }
  }
  public void removeEdge(GsRegulatoryMultiEdge multiEdge) {
    GsTreeValue val;
    GsTreeExpression exp;

    if (multiEdge.getTarget().equals(node)) {
      for (int i = 0; i < root.getChildCount(); i++) {
        val = (GsTreeValue) root.getChild(i);
        for (int j = 0; j < val.getChildCount(); j++) {
          exp = (GsTreeExpression) val.getChild(j);
          if (exp.remove(multiEdge) == null) {
            val.removeChild(j);
            j--;
          }
          else
            updateExpression((short)val.getValue(), exp);
          fireTreeStructureChanged(root);
        }
        if (val.getChildCount() == 0) {
          root.removeChild(i);
          i--;
        }
      }
    }
  }
  public void addEdge(GsRegulatoryMultiEdge multiEdge) {
    GsTreeValue val;
    GsTreeExpression exp;

    if (multiEdge.getTarget().equals(node)) {
      for (int i = 0; i < root.getChildCount(); i++) {
        val = (GsTreeValue) root.getChild(i);
        for (int j = 0; j < val.getChildCount(); j++) {
          exp = (GsTreeExpression) val.getChild(j);
          updateExpression((short)val.getValue(), exp);
          fireTreeStructureChanged(root);
        }
      }
    }
  }
  public void setActivesEdges(Vector edgeIndex, int value) {
    GsLogicalParameter inter = new GsLogicalParameter(value);
    inter.setEdges(edgeIndex);
    node.addLogicalParameter(inter);
    v_ok.add(v_ok.size()-1, Boolean.TRUE);
    fireTreeStructureChanged(root);
  }

  public void addValue(short v) {
    GsTreeValue val = new GsTreeValue(root, v);
    root.addChild(val);
  }

  private GsTreeExpression addExpression(short v, TBooleanTreeNode boolRoot) {
    GsTreeValue value;
    GsTreeExpression expression;
    for (int i = 0; i < root.getChildCount(); i++) {
      value = (GsTreeValue)root.getChild(i);
      if (value.getValue() == v) {
        expression = new GsTreeExpression(value, boolRoot);
        value.addChild(expression);
        return expression;
      }
    }
    return null;
  }

  public void addExpression(short val, GsRegulatoryVertex currentVertex, GsBooleanParser parser) throws Exception {
    TBooleanTreeNode root = parser.getRoot();
    GsLogicalFunctionList functionList = (GsLogicalFunctionList)parser.eval();
    Vector params = parser.getParams((Vector)functionList.getData());
    Iterator it = params.iterator(), it2;
    Vector v;
    GsEdgeIndex edgeIndex;
    GsLogicalFunctionListElement element;
    GsTreeFunction func;

    setNode(currentVertex);
    addValue(val);
    GsTreeExpression exp = addExpression(val, root);
    while (it.hasNext()) {
      it2 = ((Vector)it.next()).iterator();
      v = new Vector();
      while (it2.hasNext()) {
        element = (GsLogicalFunctionListElement)it2.next();
        edgeIndex = new GsEdgeIndex(element.getEdge(), element.getIndex());
        v.addElement(edgeIndex);
      }
      if (v.size() > 0) setActivesEdges(v, val);
      func = new GsTreeFunction(exp, v);
      exp.addChild(func);
    }
    parseFunctions();
    currentVertex.setInteractionsModel(this);
  }

  private void updateExpression(short val, GsTreeExpression exp) {
    updateExpression(val, exp, exp.getRoot().toString());
  }

  public void updateExpression(short val, GsTreeExpression exp, String newExp) {
    try {
      TBooleanTreeNode root = exp.getRoot();
      GsBooleanParser parser = new GsBooleanParser(graph.getGraphManager().getIncomingEdges(node));
      if (!parser.compile(newExp)) {
        graph.addNotificationMessage(new GsGraphNotificationMessage(graph, "invalid formula : " + newExp,
          GsGraphNotificationMessage.NOTIFICATION_WARNING_LONG));
      }
      else {
        root = parser.getRoot();
        exp.clearChilds();
        GsLogicalFunctionList functionList = (GsLogicalFunctionList)parser.eval();
        Vector params = parser.getParams((Vector)functionList.getData());
        Iterator it = params.iterator();
        while (it.hasNext()) {
          Iterator it2 = ((Vector)it.next()).iterator();
          Vector v = new Vector();
          while (it2.hasNext()) {
            GsLogicalFunctionListElement element = (GsLogicalFunctionListElement)it2.next();
            GsEdgeIndex edgeIndex = new GsEdgeIndex(element.getEdge(), element.getIndex());
            v.addElement(edgeIndex);
          }
          if (v.size() > 0) setActivesEdges(v, val);
          GsTreeFunction func = new GsTreeFunction(exp, v);
          exp.addChild(func);
        }
        parseFunctions();

        exp.setRoot(root);
        fireTreeStructureChanged(this.root);
        graph.getVertexAttributePanel().setEditedObject(node);
      }
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
  }

  public void fireTreeStructureChanged(GsTreeElement element) {
    TreeModelEvent e = new TreeModelEvent(this, new Object[] {element});
    for (Iterator it = treeModelListeners.iterator(); it.hasNext(); ) {
      ((TreeModelListener)it.next()).treeStructureChanged(e);
    }
  }
  public void refreshVertex() {
    parseFunctions();
    if (node != null) {
      node.setInteractionsModel(this);
      graph.getVertexAttributePanel().setEditedObject(node);
    }
  }
  public Vector getLogicalParameters() {
    Vector v = new Vector();
    GsLogicalParameter p;
    GsTreeValue val;
    GsTreeExpression exp;
    GsTreeFunction func;

    for (int i = 0; i < root.getChildCount(); i++) {
      val = (GsTreeValue)root.getChild(i);
      for (int j = 0; j < val.getChildCount(); j++) {
        exp = (GsTreeExpression)val.getChild(j);
        for (int k = 0; k < exp.getChildCount(); k++) {
          func = (GsTreeFunction)exp.getChild(k);
          if (func.isSelected() && !func.isError()) {
            p = new GsLogicalParameter(val.getValue());
            p.setEdges(func.getEdgeIndexes());
            v.addElement(p);
          }
        }
      }
    }
    return v;
  }
  public void parseFunctions() {
    Hashtable h = new Hashtable();
    GsTreeValue val;
    GsTreeExpression exp;
    GsTreeFunction func;
    int v;

    for (int i = 0; i < root.getChildCount(); i++) {
      val = (GsTreeValue)root.getChild(i);
      for (int j = 0; j < val.getChildCount(); j++) {
        exp = (GsTreeExpression)val.getChild(j);
        for (int k = 0; k < exp.getChildCount(); k++) {
          func = (GsTreeFunction)exp.getChild(k);
          if (func.isSelected()) {
            if (h.get(func.toString()) == null)
              h.put(func.toString(), new Integer(val.getValue()));
            else {
              v = ((Integer) h.get(func.toString())).intValue();
              if (Math.abs(v) == val.getValue())
                h.put(func.toString(), new Integer(-Math.abs(v)));
              else
                h.put(func.toString(), new Integer(123456));
            }
          }
        }
      }
    }
    for (int i = 0; i < root.getChildCount(); i++) {
      val = (GsTreeValue)root.getChild(i);
      for (int j = 0; j < val.getChildCount(); j++) {
        exp = (GsTreeExpression)val.getChild(j);
        for (int k = 0; k < exp.getChildCount(); k++) {
          func = (GsTreeFunction)exp.getChild(k);
          if (func.isSelected()) {
            if (((Integer) h.get(func.toString())).intValue() == 123456) {
              func.setError(true);
              func.setForeground(Color.red);
            }
            else if (((Integer) h.get(func.toString())).intValue() < 0) {
              func.setError(true);
              func.setForeground(Color.magenta);
            }
            else {
              func.setError(false);
              func.setForeground(Color.black);
            }
          }
          else {
            func.setError(false);
            func.setForeground(Color.black);
          }
        }
      }
    }
  }

  public void checkParams(short v, String chk, String exp) {
    GsTreeValue tval;
    GsTreeExpression texp;
    GsTreeFunction tfunc;

    for (int i = 0; i < root.getChildCount(); i++) {
      tval = (GsTreeValue)root.getChild(i);
      if ((short)tval.getValue() == v)
        for (int j = 0; j < tval.getChildCount(); j++) {
          texp = (GsTreeExpression)tval.getChild(j);
          if (texp.toString().equals(exp))
            for (int k = 0; k < texp.getChildCount(); k++) {
              tfunc = (GsTreeFunction)texp.getChild(k);
              tfunc.setSelected(chk.charAt(k) == '1');
            }
        }
    }
  }

  public TreePath getPath(short v, String e) {
    Object [] path = new Object[3];
    GsTreeValue tval;
    GsTreeExpression texp;

    path[0] = root;
    for (int i = 0; i < root.getChildCount(); i++) {
      tval = (GsTreeValue)root.getChild(i);
      if ((short)tval.getValue() == v)
        path[1] = tval;
        for (int j = 0; j < tval.getChildCount(); j++) {
          texp = (GsTreeExpression)tval.getChild(j);
          if (texp.toString().equals(e))
            path[2] = texp;
        }
    }
    return new TreePath(path);
  }

  //////////////// TreeModel interface implementation ///////////////////////

  public void addTreeModelListener(TreeModelListener l) {
    treeModelListeners.addElement(l);
  }

  public Object getChild(Object parent, int index) {
    return ((GsTreeElement)parent).getChild(index);
  }

  public int getChildCount(Object parent) {
    return ((GsTreeElement)parent).getChildCount();
  }

  public int getIndexOfChild(Object parent, Object child) {
    int i = 0;
    for (i = 0; i < ((GsTreeElement)parent).getChildCount(); i++)
      if (((GsTreeElement)parent).getChild(i).compareTo(child) == 0) break;
    if (i < ((GsTreeElement)parent).getChildCount()) return i;
    return 0;
  }

  public Object getRoot() {
    return root;
  }

  public boolean isLeaf(Object node) {
    return ((GsTreeElement)node).isLeaf();
  }

  public void removeTreeModelListener(TreeModelListener l) {
    treeModelListeners.removeElement(l);
  }

  public void valueForPathChanged(TreePath path, Object newValue) {
    //System.out.println("*** valueForPathChanged : " + path + " --> " + newValue);
  }
}
