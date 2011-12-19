package org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.ginsim.core.graph.regulatorygraph.RegulatoryEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryMultiEdge;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.BooleanParser;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.LogicalFunctionList;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.LogicalFunctionListElement;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.LogicalParameter;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.datamodel.TreeElement;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.datamodel.TreeExpression;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.datamodel.TreeParam;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.datamodel.TreeString;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.datamodel.TreeValue;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.param2function.FunctionsCreator;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.parser.TBooleanTreeNode;
import org.ginsim.core.notification.NotificationManager;
import org.ginsim.gui.GUIManager;


public class TreeInteractionsModel implements TreeModel {
	//the current selected node
	private RegulatoryNode node;
	private RegulatoryGraph graph;
	private TreeString root;

	private LogicalFunctionView view = null;
	private Vector treeModelListeners = new Vector();

	public TreeInteractionsModel(RegulatoryGraph graph) {
		root = new TreeString(null, "Function list");
		root.setProperty("add", new Boolean(true));
		this.graph = graph;
	}
	public void setView(LogicalFunctionView v) {
		view = v;
	}
	public void refreshView() {
		view.refresh();
	}
	public void clear() {
		root = new TreeString(null, "Function list");
		root.setProperty("add", new Boolean(true));
	}
	public void setNode(RegulatoryNode no) {
		node = no;
		for (int i = 0; i < root.getChildCount(); i++) {
			TreeValue val = (TreeValue)root.getChild(i);
			for (int j = 0; j < val.getChildCount(); j++) {
				TreeExpression exp = (TreeExpression)val.getChild(j);
				exp.refreshRoot();
			}
		}
	}
	public void removeEdge(RegulatoryMultiEdge multiEdge, int index) {
		TreeValue val;
		TreeElement exp;

		for (int i = 0; i < root.getChildCount(); i++) {
			val = (TreeValue)root.getChild(i);
			for (int j = 0; j < val.getChildCount(); j++) {
				exp = val.getChild(j);
				if (exp instanceof TreeExpression)
					if (((TreeExpression)exp).remove(multiEdge, index) == null) {
						val.removeChild(j);
						j--;
					}
					else
						setExpression((byte)val.getValue(), (TreeExpression)exp);
			}
			fireTreeStructureChanged(root);
		}
	}

	public void removeEdge(RegulatoryMultiEdge multiEdge) {
		TreeValue val;
		TreeElement exp;

		if (multiEdge.getTarget().equals(node))
			for (int i = 0; i < root.getChildCount(); i++) {
				val = (TreeValue) root.getChild(i);
				for (int j = 0; j < val.getChildCount(); j++) {
					exp = val.getChild(j);
					if (exp instanceof TreeExpression)
						if (((TreeExpression)exp).remove(multiEdge) == null) {
							val.removeChild(j);
							j--;
						}
						else
							setExpression((byte)val.getValue(), (TreeExpression)exp);
					fireTreeStructureChanged(root);
				}
				if (val.getChildCount() == 0) {
					root.removeChild(i);
					i--;
				}
			}
	}
	public void addEdge(RegulatoryMultiEdge multiEdge) {
		TreeValue val;
		TreeElement exp;

		if (multiEdge.getTarget().equals(node))
			for (int i = 0; i < root.getChildCount(); i++) {
				val = (TreeValue) root.getChild(i);
				for (int j = 0; j < val.getChildCount(); j++) {
					exp = val.getChild(j);
					if (exp instanceof TreeExpression) {
						setExpression((byte)val.getValue(), (TreeExpression)exp);
						fireTreeStructureChanged(root);
					}
				}
			}
	}
	public void setActivesEdges(Vector edgeIndex, int value) {
		LogicalParameter inter = new LogicalParameter(value);
		inter.setEdges(edgeIndex);
		node.addLogicalParameter(inter, false);
		fireTreeStructureChanged(root);
	}

	public void addValue(byte v) {
		TreeValue val = new TreeValue(root, v);
		root.addChild(val, -1);
	}

	public void addValue(TreeValue v) {
		root.addChild(v, -1);
		v.setParent(root);
	}

	public void removeNullFunction(byte v) {
		TreeValue value;
		TreeElement expression;
		for (int i = 0; i < root.getChildCount(); i++) {
			value = (TreeValue)root.getChild(i);
			if (value.getValue() == v)
				for (int j = 0; j < value.getChildCount(); j++) {
					expression = value.getChild(j);
					if (expression instanceof TreeExpression)
						if (((TreeExpression)expression).getRoot() == null) {
							expression.remove(false);
							break;
						}
				}
		}
	}

	private TreeExpression addExpression(byte v, TBooleanTreeNode boolRoot) {
		TreeValue value;
		TreeExpression expression;
		for (int i = 0; i < root.getChildCount(); i++) {
			value = (TreeValue)root.getChild(i);
			if (value.getValue() == v) {
				expression = new TreeExpression(value, boolRoot, new FunctionsCreator(graph, null, node));
				expression = (TreeExpression)value.addChild(expression, -1);
				return expression;
			}
		}
		return null;
	}

	public void addExpression(byte val, RegulatoryNode currentNode, BooleanParser parser) throws Exception {

		TBooleanTreeNode root = parser.getRoot();
		LogicalFunctionList functionList = (LogicalFunctionList)parser.eval();
		Vector params = parser.getParams(functionList.getData());
		Iterator it = params.iterator(), it2;
		Vector v;
		RegulatoryEdge edge;
		LogicalFunctionListElement element;
		TreeParam param;

		setNode(currentNode);
		addValue(val);
		TreeExpression exp = addExpression(val, root);
		if (exp != null)
			while (it.hasNext()) {
				it2 = ((Vector)it.next()).iterator();
				v = new Vector();
				while (it2.hasNext()) {
					element = (LogicalFunctionListElement)it2.next();
					edge = element.getEdge().getEdge(element.getIndex());
					v.addElement(edge);
				}
				if (v.size() > 0) setActivesEdges(v, val);
				param = new TreeParam(exp, v);
				exp.addChild(param, -1);
			}
		parseFunctions();
		currentNode.setInteractionsModel(this);
	}
	public boolean isBasalValueDefined() {
		List parameters = getLogicalParameters();
		LogicalParameter p;
		boolean b = false;

		for (Iterator enu = parameters.iterator(); enu.hasNext(); ) {
			p = (LogicalParameter)enu.next();
			b |= p.EdgeCount() == 0;
		}
		return b;
	}
	public int getNbFunctions() {
		int n = 0;
		TreeValue val;

		for (int i = 0; i < root.getChildCount(); i++) {
			val = (TreeValue)root.getChild(i);
			n += val.getChildCount();
		}
		return n;
	}
	public void addExpression(JTree tree, byte val, RegulatoryNode currentNode, String expression) throws Exception {
		BooleanParser tbp = new BooleanParser(graph.getIncomingEdges(currentNode), isAutoAddEnabled());
		if (!tbp.compile(expression, graph, currentNode))
			NotificationManager.publishWarning( graph, "invalid formula");
		else {
			addExpression(val, currentNode, tbp);
			fireTreeStructureChanged(root);
			if (tree != null) tree.expandPath(getPath(val, tbp.getRoot().toString(false)));
			GUIManager.getInstance().getGraphGUI(graph).getNodeEditionPanel().setEditedItem(currentNode);
		}
	}
	public void setRootInfos() {
		String s = getNbFunctions() + " functions, " + getLogicalParameters().size() + " parameters";
		if (isBasalValueDefined()) s = s + ", basal value defined";
		root.setString(s);
	}
	public TreeExpression addEmptyExpression(byte val, RegulatoryNode currentNode) throws Exception {
		setNode(currentNode);
		addValue(val);
		currentNode.setInteractionsModel(this);
		return addExpression(val, (TBooleanTreeNode)null);
	}
	public TreeParam addEmptyParameter(byte val, RegulatoryNode currentNode) throws Exception {
		TreeParam param = null;

		setNode(currentNode);
		addValue(val);
		return param;
	}
	private void setExpression(byte val, TreeExpression exp) {
		if (exp.getRoot() != null) updateExpression(val, exp, exp.getRoot().toString(false));
	}

	public void updateValue(byte newVal, byte oldVal) {
		TreeValue value;
		for (int i = 0; i < root.getChildCount(); i++) {
			value = (TreeValue)root.getChild(i);
			if ((byte)value.getValue() == oldVal) {
				value.setValue(newVal);
				break;
			}
		}
		refreshNode();
	}

	private boolean isAutoAddEnabled() {
		// FIXME: find a better way to know if we should autoadd elements
		// return graph.getGraphManager().getMainFrame().getActions().shouldAutoAddNewElements();
		return false;
	}
	
	public boolean updateExpression(byte val, TreeExpression exp, String newExp) {
		try {
			TBooleanTreeNode root = exp.getRoot();
			if (newExp.equals("")) {
				exp.clearChilds();
				fireTreeStructureChanged(this.root);
				GUIManager.getInstance().getGraphGUI(graph).getNodeEditionPanel().setEditedItem(node);
				return true;
			}
			BooleanParser parser = new BooleanParser(graph.getIncomingEdges(node), isAutoAddEnabled());
			if (!parser.compile(newExp.trim(), graph, node)) {
				NotificationManager.publishWarning( graph, "invalid formula : " + newExp);
				exp.clearChilds();
				exp.setProperty("invalid", new Boolean(true));
				return false;
			}
			root = parser.getRoot();
			exp.clearChilds();
			LogicalFunctionList functionList = (LogicalFunctionList)parser.eval();
			Vector params = parser.getParams(functionList.getData());
			Iterator it = params.iterator();
			TreeParam paramBasal = null;
			while (it.hasNext()) {
				Iterator it2 = ((Vector)it.next()).iterator();
				Vector v = new Vector();
				while (it2.hasNext()) {
					LogicalFunctionListElement element = (LogicalFunctionListElement)it2.next();
					v.addElement(element.getEdge().getEdge(element.getIndex()));
				}
				if (v.size() > 0) setActivesEdges(v, val);
				TreeParam param = new TreeParam(exp, v);
				if (param.isBasal()) 
					paramBasal = param;
				else
					exp.addChild(param, -1);
			}
			if (paramBasal != null)	exp.addChild(paramBasal, 0);
			parseFunctions();
			exp.setRoot(root);
			fireTreeStructureChanged(this.root);
			GUIManager.getInstance().getGraphGUI(graph).getNodeEditionPanel().setEditedItem(node);
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		return true;
	}

	public void fireTreeStructureChanged(TreeElement element) {
		TreeModelEvent e = new TreeModelEvent(this, new Object[] {element});
		for (Iterator it = treeModelListeners.iterator(); it.hasNext(); ) ((TreeModelListener)it.next()).treeStructureChanged(e);
	}
	public void refreshNode() {
		boolean dis = false;

		parseFunctions();
		if (node != null) {
			node.setInteractionsModel(this);
			GUIManager.getInstance().getGraphGUI(graph).getNodeEditionPanel().setEditedItem(node);
			for (int p = 0 ; p <= node.getMaxValue(); p++) {
				dis = false;
				for (int k = 0; k < root.getChildCount(); k++)
					if (((TreeValue) root.getChild(k)).getValue() == p) {
						dis = true;
						break;
					}
				if (!dis) break;
			}
			root.setProperty("add", new Boolean(!dis));
		}
	}
	public List getLogicalParameters() {
		List v = new ArrayList();
		List v2 = new ArrayList();
		LogicalParameter p;
		TreeValue val;
		TreeElement exp;
		TreeParam param;
		v.clear();

		for (int i = 0; i < root.getChildCount(); i++) {
			val = (TreeValue)root.getChild(i);
			for (int j = 0; j < val.getChildCount(); j++) {
				exp = val.getChild(j);
				for (int k = 0; k < exp.getChildCount(); k++) {
					param = (TreeParam)exp.getChild(k);
					if (!param.isError()) {
						p = new LogicalParameter(val.getValue());
						p.setEdges(param.getEdgeIndexes());
						if (!(param.isWarning() && v2.contains(p.toString()))) v.add(p);
						if (param.isWarning()) v2.add(p.toString());
					}
				}
			}
		}
		return v;
	}
	public void parseFunctions() {
		Hashtable h = new Hashtable();
		TreeValue val;
		TreeElement exp;
		TreeParam param;
		int v;

		for (int i = 0; i < root.getChildCount(); i++) {
			val = (TreeValue)root.getChild(i);
			for (int j = 0; j < val.getChildCount(); j++) {
				exp = val.getChild(j);
				for (int k = 0; k < exp.getChildCount(); k++) {
					param = (TreeParam)exp.getChild(k);
					if (h.get(param.toString()) == null)
						h.put(param.toString(), new Integer(val.getValue()));
					else {
						v = ((Integer) h.get(param.toString())).intValue();
						if (Math.abs(v) == val.getValue())
							h.put(param.toString(), new Integer(-Math.abs(v)));
						else
							h.put(param.toString(), new Integer(123456));
					}
				}
			}
		}
		for (int i = 0; i < root.getChildCount(); i++) {
			val = (TreeValue)root.getChild(i);
			for (int j = 0; j < val.getChildCount(); j++) {
				exp = val.getChild(j);
				for (int k = 0; k < exp.getChildCount(); k++) {
					param = (TreeParam)exp.getChild(k);
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

	public TreePath getPath(byte v, String e) {
		Object [] path = new Object[3];
		TreeValue tval;
		TreeElement texp;

		path[0] = root;
		for (int i = 0; i < root.getChildCount(); i++) {
			tval = (TreeValue)root.getChild(i);
			if ((byte)tval.getValue() == v) path[1] = tval;
			for (int j = 0; j < tval.getChildCount(); j++) {
				texp = tval.getChild(j);
				if (texp.toString().equals(e)) path[2] = texp;
			}
		}
		return new TreePath(path);
	}
	public RegulatoryNode getNode() {
		return node;
	}
	public boolean isMaxCompatible(int max) {
		boolean comp = true;
		TreeValue value;

		for (int i = 0; i < root.getChildCount(); i++) {
			value = (TreeValue)root.getChild(i);
			if (value.getValue() > max) {
				comp = false;
				break;
			}
		}
		return comp;
	}

	public RegulatoryGraph getGraph() {
		return graph;
	}

	//////////////// TreeModel interface implementation ///////////////////////

	public void addTreeModelListener(TreeModelListener l) {
		treeModelListeners.addElement(l);
	}

	public Object getChild(Object parent, int index) {
		return ((TreeElement)parent).getChild(index);
	}

	public int getChildCount(Object parent) {
		return ((TreeElement)parent).getChildCount();
	}

	public int getIndexOfChild(Object parent, Object child) {
		int i = 0;
		for (i = 0; i < ((TreeElement)parent).getChildCount(); i++)
			if (((TreeElement)parent).getChild(i).compareTo(child) == 0) break;
		if (i < ((TreeElement)parent).getChildCount()) return i;
		return 0;
	}

	public Object getRoot() {
		return root;
	}

	public boolean isLeaf(Object node) {
		return ((TreeElement)node).isLeaf();
	}

	public void removeTreeModelListener(TreeModelListener l) {
		treeModelListeners.removeElement(l);
	}

	public void valueForPathChanged(TreePath path, Object newValue) {
		//System.out.println("*** valueForPathChanged : " + path + " --> " + newValue);
	}
}
