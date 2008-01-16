package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree;

import java.awt.Color;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import fr.univmrs.ibdm.GINsim.graph.GsGraphNotificationMessage;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.*;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.GsBooleanParser;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.GsLogicalFunctionList;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.GsLogicalFunctionListElement;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.GsLogicalFunctionTreePanel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.*;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.param2function.GsFunctionsCreator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser.TBooleanTreeNode;

public class GsTreeInteractionsModel implements TreeModel {
  //the vector of interaction
  private Vector interactions;

  //the current selected node
  private GsRegulatoryVertex node;
  private GsRegulatoryGraph graph;
  private GsTreeString root;

  private GsLogicalFunctionTreePanel view = null;
  private Vector treeModelListeners = new Vector();

  public GsTreeInteractionsModel(GsRegulatoryGraph graph) {
    root = new GsTreeString(null, "Function list");
    root.setProperty("add", new Boolean(true));
    interactions = null;
    this.graph = graph;
  }
  public void setView(GsLogicalFunctionTreePanel v) {
    view = v;
  }
  public void refreshView() {
    view.refresh();
  }
  public void clear() {
    root = new GsTreeString(null, "Function list");
    root.setProperty("add", new Boolean(true));
    interactions = null;
  }
  public void setNode(GsRegulatoryVertex no) {
    node = no;
    if (node != null) {
		interactions = node.getV_logicalParameters();
	} else {
		interactions = null;
	}
  }
  public void removeEdge(GsRegulatoryMultiEdge multiEdge, int index) {
    GsTreeValue val;
    GsTreeElement exp;

    for (int i = 0; i < root.getChildCount(); i++) {
      val = (GsTreeValue)root.getChild(i);
      for (int j = 0; j < val.getChildCount(); j++) {
        exp = val.getChild(j);
        if (exp instanceof GsTreeExpression) {
			if (((GsTreeExpression)exp).remove(multiEdge, index) == null) {
			    val.removeChild(j);
			    j--;
			  } else {
				setExpression((short)val.getValue(), (GsTreeExpression)exp);
			}
		}
        fireTreeStructureChanged(root);
      }
    }
  }
  public void removeEdge(GsRegulatoryMultiEdge multiEdge) {
    GsTreeValue val;
    GsTreeElement exp;

    if (multiEdge.getTarget().equals(node)) {
		for (int i = 0; i < root.getChildCount(); i++) {
		    val = (GsTreeValue) root.getChild(i);
		    for (int j = 0; j < val.getChildCount(); j++) {
		      exp = val.getChild(j);
		      if (exp instanceof GsTreeExpression) {
				if (((GsTreeExpression)exp).remove(multiEdge) == null) {
		          val.removeChild(j);
		          j--;
		        } else {
					setExpression((short)val.getValue(), (GsTreeExpression)exp);
				}
			}
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
    GsTreeElement exp;

    if (multiEdge.getTarget().equals(node)) {
      for (int i = 0; i < root.getChildCount(); i++) {
        val = (GsTreeValue) root.getChild(i);
        for (int j = 0; j < val.getChildCount(); j++) {
          exp = val.getChild(j);
          if (exp instanceof GsTreeExpression) {
            setExpression((short)val.getValue(), (GsTreeExpression)exp);
            fireTreeStructureChanged(root);
          }
        }
      }
    }
  }
  public void setActivesEdges(Vector edgeIndex, int value) {
    GsLogicalParameter inter = new GsLogicalParameter(value, false);
    inter.setEdges(edgeIndex);
    node.addLogicalParameter(inter);
    fireTreeStructureChanged(root);
  }

  public void addValue(short v) {
    GsTreeValue val = new GsTreeValue(root, v);
    root.addChild(val, -1);
    GsTreeValue value;
    for (int i = 0; i < root.getChildCount(); i++) {
      value = (GsTreeValue)root.getChild(i);
    }
  }

  public void addValue(GsTreeValue v) {
    root.addChild(v, -1);
    v.setParent(root);
  }

  public void removeNullFunction(short v) {
    GsTreeValue value;
    GsTreeElement expression;
    for (int i = 0; i < root.getChildCount(); i++) {
      value = (GsTreeValue)root.getChild(i);
      if (value.getValue() == v) {
        for (int j = 0; j < value.getChildCount(); j++) {
          expression = value.getChild(j);
          if (expression instanceof GsTreeExpression) {
            if (((GsTreeExpression)expression).getRoot() == null) {
              expression.remove(false);
              break;
            }
          }
        }
      }
    }
  }

  private GsTreeExpression addExpression(short v, TBooleanTreeNode boolRoot) {
    GsTreeValue value;
    GsTreeExpression expression;
    for (int i = 0; i < root.getChildCount(); i++) {
      value = (GsTreeValue)root.getChild(i);
      if (value.getValue() == v) {
        expression = new GsTreeExpression(value, boolRoot, new GsFunctionsCreator(graph.getGraphManager(),
            null, node));
        expression = (GsTreeExpression)value.addChild(expression, -1);
        return expression;
      }
    }
    return null;
  }

  public void addExpression(short val, GsRegulatoryVertex currentVertex, GsBooleanParser parser) throws Exception {

    TBooleanTreeNode root = parser.getRoot();
    GsLogicalFunctionList functionList = (GsLogicalFunctionList)parser.eval();
    Vector params = parser.getParams(functionList.getData());
    Iterator it = params.iterator(), it2;
    Vector v;
    GsRegulatoryEdge edge;
    GsLogicalFunctionListElement element;
    GsTreeParam param;

    setNode(currentVertex);
    addValue(val);
    GsTreeExpression exp = addExpression(val, root);
    while (it.hasNext()) {
      it2 = ((Vector)it.next()).iterator();
      v = new Vector();
      while (it2.hasNext()) {
        element = (GsLogicalFunctionListElement)it2.next();
        edge = element.getEdge().getEdge(element.getIndex() - 1);
        v.addElement(edge);
      }
      if (v.size() > 0) {
		setActivesEdges(v, val);
	}
      param = new GsTreeParam(exp, v);
      exp.addChild(param, -1);
    }
    parseFunctions();
    currentVertex.setInteractionsModel(this);
  }
  public boolean isBasalValueDefined() {
    Vector parameters = getLogicalParameters();
    GsLogicalParameter p;
    boolean b = false;

    for (Enumeration enu = parameters.elements(); enu.hasMoreElements(); ) {
      p = (GsLogicalParameter)enu.nextElement();
      b |= p.EdgeCount() == 0;
    }
    return b;
  }
  public int getNbFunctions() {
    int n = 0;
    GsTreeValue val;

    for (int i = 0; i < root.getChildCount(); i++) {
      val = (GsTreeValue)root.getChild(i);
      n += val.getChildCount();
    }
    return n;
  }
  public void addExpression(JTree tree, short val, GsRegulatoryVertex currentVertex, String expression) throws Exception {
    GsBooleanParser tbp = new GsBooleanParser(graph.getGraphManager().getIncomingEdges(currentVertex));
    if (!tbp.compile(expression)) {
      graph.addNotificationMessage(new GsGraphNotificationMessage(graph, "invalid formula",
        GsGraphNotificationMessage.NOTIFICATION_WARNING));
    }
    else {
      addExpression(val, currentVertex, tbp);
      fireTreeStructureChanged(root);
      if (tree != null) {
		tree.expandPath(getPath(val, tbp.getRoot().toString()));
	}
      graph.getVertexEditor().setEditedObject(currentVertex);
    }
  }
  public void setRootInfos() {
    String s = getNbFunctions() + " functions, " + getLogicalParameters().size() + " parameters";
    if (isBasalValueDefined()) {
		s = s + ", basal value defined";
	}
    root.setString(s);
  }
  public GsTreeExpression addEmptyExpression(short val, GsRegulatoryVertex currentVertex) throws Exception {
    setNode(currentVertex);
    addValue(val);
    currentVertex.setInteractionsModel(this);
    return addExpression(val, (TBooleanTreeNode)null);
  }
  public GsTreeParam addEmptyParameter(short val, GsRegulatoryVertex currentVertex) throws Exception {
    GsTreeValue value;
    GsTreeParam param = null;

    setNode(currentVertex);
    addValue(val);
    for (int i = 0; i < root.getChildCount(); i++) {
      value = (GsTreeValue)root.getChild(i);
    }
    return param;
  }
  private void setExpression(short val, GsTreeExpression exp) {
    if (exp.getRoot() != null) {
		updateExpression(val, exp, exp.getRoot().toString());
	}
  }

  public void updateValue(short newVal, short oldVal) {
    GsTreeValue value;
    for (int i = 0; i < root.getChildCount(); i++) {
      value = (GsTreeValue)root.getChild(i);
      if ((short)value.getValue() == oldVal) {
        value.setValue(newVal);
        break;
      }
    }
    refreshVertex();
  }

  public boolean updateExpression(short val, GsTreeExpression exp, String newExp) {
    try {
      TBooleanTreeNode root = exp.getRoot();
      GsBooleanParser parser = new GsBooleanParser(graph.getGraphManager().getIncomingEdges(node));
      if (newExp.equals("")) {
        exp.clearChilds();
        fireTreeStructureChanged(this.root);
        graph.getVertexEditor().setEditedObject(node);
        return true;
      }
      if (!parser.compile(newExp.trim())) {
        graph.addNotificationMessage(new GsGraphNotificationMessage(graph, "invalid formula : " + newExp,
            GsGraphNotificationMessage.NOTIFICATION_WARNING_LONG));
        exp.clearChilds();
        exp.setProperty("invalid", new Boolean(true));
        return false;
      }
      root = parser.getRoot();
      exp.clearChilds();
      GsLogicalFunctionList functionList = (GsLogicalFunctionList)parser.eval();
      Vector params = parser.getParams(functionList.getData());
      Iterator it = params.iterator();
      while (it.hasNext()) {
        Iterator it2 = ((Vector)it.next()).iterator();
        Vector v = new Vector();
        while (it2.hasNext()) {
          GsLogicalFunctionListElement element = (GsLogicalFunctionListElement)it2.next();
          v.addElement(element.getEdge().getEdge(element.getIndex() - 1));
        }
        if (v.size() > 0) {
			setActivesEdges(v, val);
		}
        GsTreeParam param = new GsTreeParam(exp, v);
        exp.addChild(param, -1);
      }
      parseFunctions();
      exp.setRoot(root);
      fireTreeStructureChanged(this.root);
      graph.getVertexEditor().setEditedObject(node);
    }
    catch (Exception ex) {
      ex.printStackTrace();
    }
    return true;
  }

  public void fireTreeStructureChanged(GsTreeElement element) {
    TreeModelEvent e = new TreeModelEvent(this, new Object[] {element});
    for (Iterator it = treeModelListeners.iterator(); it.hasNext(); ) {
		((TreeModelListener)it.next()).treeStructureChanged(e);
	}
  }
  public void refreshVertex() {
    boolean dis = false;

    parseFunctions();
    if (node != null) {
      node.setInteractionsModel(this);
      graph.getVertexEditor().setEditedObject(node);
      for (int p = node.getBaseValue(); p <= node.getMaxValue(); p++) {
        dis = false;
        for (int k = 0; k < root.getChildCount(); k++) {
			if (((GsTreeValue) root.getChild(k)).getValue() == p) {
			    dis = true;
			    break;
			  }
		}
        if (!dis) {
			break;
		}
      }
      root.setProperty("add", new Boolean(!dis));
    }
  }
  public Vector getLogicalParameters() {
    Vector v = new Vector();
    Vector v2 = new Vector();
    GsLogicalParameter p;
    GsTreeValue val;
    GsTreeElement exp;
    GsTreeParam param;

    for (int i = 0; i < root.getChildCount(); i++) {
      val = (GsTreeValue)root.getChild(i);
      for (int j = 0; j < val.getChildCount(); j++) {
        exp = val.getChild(j);
        for (int k = 0; k < exp.getChildCount(); k++) {
          param = (GsTreeParam)exp.getChild(k);
          if (!param.isError()) {
            p = new GsLogicalParameter(val.getValue(), false);
            p.setEdges(param.getEdgeIndexes());
            if (!(param.isWarning() && v2.contains(p.toString()))) {
				v.addElement(p);
			}
            if (param.isWarning()) {
				v2.addElement(p.toString());
			}
          }
        }
      }
    }
    return v;
  }
  public void parseFunctions() {
    Hashtable h = new Hashtable();
    GsTreeValue val;
    GsTreeElement exp;
    GsTreeParam param;
    int v;

    for (int i = 0; i < root.getChildCount(); i++) {
      val = (GsTreeValue)root.getChild(i);
      for (int j = 0; j < val.getChildCount(); j++) {
        exp = val.getChild(j);
        for (int k = 0; k < exp.getChildCount(); k++) {
          param = (GsTreeParam)exp.getChild(k);
          if (h.get(param.toString()) == null) {
			h.put(param.toString(), new Integer(val.getValue()));
		} else {
            v = ((Integer) h.get(param.toString())).intValue();
            if (Math.abs(v) == val.getValue()) {
				h.put(param.toString(), new Integer(-Math.abs(v)));
			} else {
				h.put(param.toString(), new Integer(123456));
			}
          }
        }
      }
    }
    for (int i = 0; i < root.getChildCount(); i++) {
      val = (GsTreeValue)root.getChild(i);
      for (int j = 0; j < val.getChildCount(); j++) {
        exp = val.getChild(j);
        for (int k = 0; k < exp.getChildCount(); k++) {
          param = (GsTreeParam)exp.getChild(k);
          if (((Integer) h.get(param.toString())).intValue() == 123456) {
            param.setError(true);
            param.setWarning(false);
            param.setForeground(Color.red);
          }
          else if (((Integer) h.get(param.toString())).intValue() < 0) {
            param.setError(false);
            param.setWarning(true);
            param.setForeground(Color.magenta);
          }
          else {
            param.setError(false);
            param.setWarning(false);
            param.setForeground(Color.black);
          }
        }
      }
    }
  }

  public TreePath getPath(short v, String e) {
    Object [] path = new Object[3];
    GsTreeValue tval;
    GsTreeElement texp;

    path[0] = root;
    for (int i = 0; i < root.getChildCount(); i++) {
      tval = (GsTreeValue)root.getChild(i);
      if ((short)tval.getValue() == v) {
		path[1] = tval;
	}
      for (int j = 0; j < tval.getChildCount(); j++) {
        texp = tval.getChild(j);
        if (texp.toString().equals(e)) {
			path[2] = texp;
		}
      }
    }
    return new TreePath(path);
  }
  public GsRegulatoryVertex getVertex() {
    return node;
  }
  public boolean isMaxCompatible(int max) {
    boolean comp = true;
    GsTreeValue value;

    for (int i = 0; i < root.getChildCount(); i++) {
      value = (GsTreeValue)root.getChild(i);
      if (value.getValue() > max) {
        comp = false;
        break;
      }
    }
    return comp;
  }

  public GsRegulatoryGraph getGraph() {
    return graph;
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
    for (i = 0; i < ((GsTreeElement)parent).getChildCount(); i++) {
		if (((GsTreeElement)parent).getChild(i).compareTo(child) == 0) {
			break;
		}
	}
    if (i < ((GsTreeElement)parent).getChildCount()) {
		return i;
	}
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
