package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import java.util.Vector;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsLogicalParameter;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser.TBooleanTreeNode;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;
import java.util.Iterator;
import javax.swing.event.TreeModelEvent;
import javax.swing.tree.TreeModel;
import java.awt.Color;
import java.util.Hashtable;

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

  public void addExpression(short v, TBooleanTreeNode boolRoot) {
    GsTreeValue value;
    GsTreeExpression expression;
    for (int i = 0; i < root.getChildCount(); i++) {
      value = (GsTreeValue)root.getChild(i);
      if (value.getValue() == v) {
        expression = new GsTreeExpression(value, boolRoot);
        value.addChild(expression);
        break;
      }
    }
  }

  public void addFunction(short v, String exp, Vector params) {
    GsTreeValue value;
    GsTreeExpression expression;
    GsTreeFunction func;
    for (int i = 0; i < root.getChildCount(); i++) {
      value = (GsTreeValue)root.getChild(i);
      if (value.getValue() == v) {
        for (int j = 0; j < value.getChildCount(); j++) {
          expression = (GsTreeExpression)value.getChild(j);
          if (expression.toString().equals(exp)) {
            func = new GsTreeFunction(expression, params);
            expression.addChild(func);
            break;
          }
        }
        break;
      }
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
              func.setForeround(Color.red);
            }
            else if (((Integer) h.get(func.toString())).intValue() < 0) {
              func.setError(true);
              func.setForeround(Color.magenta);
            }
            else {
              func.setError(false);
              func.setForeround(Color.black);
            }
          }
          else {
            func.setError(false);
            func.setForeround(Color.black);
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
