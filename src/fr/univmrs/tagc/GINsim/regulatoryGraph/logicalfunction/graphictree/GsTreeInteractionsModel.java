package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree;

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

import org.ginsim.exception.NotificationMessage;
import org.ginsim.graph.regulatorygraph.GsRegulatoryEdge;
import org.ginsim.graph.regulatorygraph.GsRegulatoryGraph;
import org.ginsim.graph.regulatorygraph.GsRegulatoryMultiEdge;
import org.ginsim.graph.regulatorygraph.GsRegulatoryVertex;
import org.ginsim.gui.GUIManager;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsLogicalParameter;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.GsBooleanParser;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.GsLogicalFunctionList;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.GsLogicalFunctionListElement;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.GsLogicalFunctionTreePanel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeExpression;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeParam;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeString;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeValue;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.param2function.GsFunctionsCreator;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.parser.TBooleanTreeNode;

public class GsTreeInteractionsModel implements TreeModel {
	//the current selected node
	private GsRegulatoryVertex node;
	private GsRegulatoryGraph graph;
	private GsTreeString root;

	private GsLogicalFunctionTreePanel view = null;
	private Vector treeModelListeners = new Vector();

	public GsTreeInteractionsModel(GsRegulatoryGraph graph) {
		root = new GsTreeString(null, "Function list");
		root.setProperty("add", new Boolean(true));
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
	}
	public void setNode(GsRegulatoryVertex no) {
		node = no;
		for (int i = 0; i < root.getChildCount(); i++) {
			GsTreeValue val = (GsTreeValue)root.getChild(i);
			for (int j = 0; j < val.getChildCount(); j++) {
				GsTreeExpression exp = (GsTreeExpression)val.getChild(j);
				exp.refreshRoot();
			}
		}
	}
	public void removeEdge(GsRegulatoryMultiEdge multiEdge, int index) {
		GsTreeValue val;
		GsTreeElement exp;

		for (int i = 0; i < root.getChildCount(); i++) {
			val = (GsTreeValue)root.getChild(i);
			for (int j = 0; j < val.getChildCount(); j++) {
				exp = val.getChild(j);
				if (exp instanceof GsTreeExpression)
					if (((GsTreeExpression)exp).remove(multiEdge, index) == null) {
						val.removeChild(j);
						j--;
					}
					else
						setExpression((byte)val.getValue(), (GsTreeExpression)exp);
			}
			fireTreeStructureChanged(root);
		}
	}

	public void removeEdge(GsRegulatoryMultiEdge multiEdge) {
		GsTreeValue val;
		GsTreeElement exp;

		if (multiEdge.getTarget().equals(node))
			for (int i = 0; i < root.getChildCount(); i++) {
				val = (GsTreeValue) root.getChild(i);
				for (int j = 0; j < val.getChildCount(); j++) {
					exp = val.getChild(j);
					if (exp instanceof GsTreeExpression)
						if (((GsTreeExpression)exp).remove(multiEdge) == null) {
							val.removeChild(j);
							j--;
						}
						else
							setExpression((byte)val.getValue(), (GsTreeExpression)exp);
					fireTreeStructureChanged(root);
				}
				if (val.getChildCount() == 0) {
					root.removeChild(i);
					i--;
				}
			}
	}
	public void addEdge(GsRegulatoryMultiEdge multiEdge) {
		GsTreeValue val;
		GsTreeElement exp;

		if (multiEdge.getTarget().equals(node))
			for (int i = 0; i < root.getChildCount(); i++) {
				val = (GsTreeValue) root.getChild(i);
				for (int j = 0; j < val.getChildCount(); j++) {
					exp = val.getChild(j);
					if (exp instanceof GsTreeExpression) {
						setExpression((byte)val.getValue(), (GsTreeExpression)exp);
						fireTreeStructureChanged(root);
					}
				}
			}
	}
	public void setActivesEdges(Vector edgeIndex, int value) {
		GsLogicalParameter inter = new GsLogicalParameter(value);
		inter.setEdges(edgeIndex);
		node.addLogicalParameter(inter, false);
		fireTreeStructureChanged(root);
	}

	public void addValue(byte v) {
		GsTreeValue val = new GsTreeValue(root, v);
		root.addChild(val, -1);
	}

	public void addValue(GsTreeValue v) {
		root.addChild(v, -1);
		v.setParent(root);
	}

	public void removeNullFunction(byte v) {
		GsTreeValue value;
		GsTreeElement expression;
		for (int i = 0; i < root.getChildCount(); i++) {
			value = (GsTreeValue)root.getChild(i);
			if (value.getValue() == v)
				for (int j = 0; j < value.getChildCount(); j++) {
					expression = value.getChild(j);
					if (expression instanceof GsTreeExpression)
						if (((GsTreeExpression)expression).getRoot() == null) {
							expression.remove(false);
							break;
						}
				}
		}
	}

	private GsTreeExpression addExpression(byte v, TBooleanTreeNode boolRoot) {
		GsTreeValue value;
		GsTreeExpression expression;
		for (int i = 0; i < root.getChildCount(); i++) {
			value = (GsTreeValue)root.getChild(i);
			if (value.getValue() == v) {
				expression = new GsTreeExpression(value, boolRoot, new GsFunctionsCreator(graph, null, node));
				expression = (GsTreeExpression)value.addChild(expression, -1);
				return expression;
			}
		}
		return null;
	}

	public void addExpression(byte val, GsRegulatoryVertex currentVertex, GsBooleanParser parser) throws Exception {

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
		if (exp != null)
			while (it.hasNext()) {
				it2 = ((Vector)it.next()).iterator();
				v = new Vector();
				while (it2.hasNext()) {
					element = (GsLogicalFunctionListElement)it2.next();
					edge = element.getEdge().getEdge(element.getIndex());
					v.addElement(edge);
				}
				if (v.size() > 0) setActivesEdges(v, val);
				param = new GsTreeParam(exp, v);
				exp.addChild(param, -1);
			}
		parseFunctions();
		currentVertex.setInteractionsModel(this);
	}
	public boolean isBasalValueDefined() {
		List parameters = getLogicalParameters();
		GsLogicalParameter p;
		boolean b = false;

		for (Iterator enu = parameters.iterator(); enu.hasNext(); ) {
			p = (GsLogicalParameter)enu.next();
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
	public void addExpression(JTree tree, byte val, GsRegulatoryVertex currentVertex, String expression) throws Exception {
		GsBooleanParser tbp = new GsBooleanParser(graph.getIncomingEdges(currentVertex), isAutoAddEnabled());
		if (!tbp.compile(expression, graph, currentVertex))
			graph.addNotificationMessage(new NotificationMessage(graph, "invalid formula",
					NotificationMessage.NOTIFICATION_WARNING));
		else {
			addExpression(val, currentVertex, tbp);
			fireTreeStructureChanged(root);
			if (tree != null) tree.expandPath(getPath(val, tbp.getRoot().toString(false)));
			GUIManager.getInstance().getGraphGUI(graph).getNodeEditionPanel().setEditedItem(currentVertex);
		}
	}
	public void setRootInfos() {
		String s = getNbFunctions() + " functions, " + getLogicalParameters().size() + " parameters";
		if (isBasalValueDefined()) s = s + ", basal value defined";
		root.setString(s);
	}
	public GsTreeExpression addEmptyExpression(byte val, GsRegulatoryVertex currentVertex) throws Exception {
		setNode(currentVertex);
		addValue(val);
		currentVertex.setInteractionsModel(this);
		return addExpression(val, (TBooleanTreeNode)null);
	}
	public GsTreeParam addEmptyParameter(byte val, GsRegulatoryVertex currentVertex) throws Exception {
		GsTreeParam param = null;

		setNode(currentVertex);
		addValue(val);
		return param;
	}
	private void setExpression(byte val, GsTreeExpression exp) {
		if (exp.getRoot() != null) updateExpression(val, exp, exp.getRoot().toString(false));
	}

	public void updateValue(byte newVal, byte oldVal) {
		GsTreeValue value;
		for (int i = 0; i < root.getChildCount(); i++) {
			value = (GsTreeValue)root.getChild(i);
			if ((byte)value.getValue() == oldVal) {
				value.setValue(newVal);
				break;
			}
		}
		refreshVertex();
	}

	private boolean isAutoAddEnabled() {
		// FIXME: find a better way to know if we should autoadd elements
		// return graph.getGraphManager().getMainFrame().getActions().shouldAutoAddNewElements();
		return false;
	}
	
	public boolean updateExpression(byte val, GsTreeExpression exp, String newExp) {
		try {
			TBooleanTreeNode root = exp.getRoot();
			if (newExp.equals("")) {
				exp.clearChilds();
				fireTreeStructureChanged(this.root);
				GUIManager.getInstance().getGraphGUI(graph).getNodeEditionPanel().setEditedItem(node);
				return true;
			}
			GsBooleanParser parser = new GsBooleanParser(graph.getIncomingEdges(node), isAutoAddEnabled());
			if (!parser.compile(newExp.trim(), graph, node)) {
				graph.addNotificationMessage(new NotificationMessage(graph, "invalid formula : " + newExp,
						NotificationMessage.NOTIFICATION_WARNING_LONG));
				exp.clearChilds();
				exp.setProperty("invalid", new Boolean(true));
				return false;
			}
			root = parser.getRoot();
			exp.clearChilds();
			GsLogicalFunctionList functionList = (GsLogicalFunctionList)parser.eval();
			Vector params = parser.getParams(functionList.getData());
			Iterator it = params.iterator();
			GsTreeParam paramBasal = null;
			while (it.hasNext()) {
				Iterator it2 = ((Vector)it.next()).iterator();
				Vector v = new Vector();
				while (it2.hasNext()) {
					GsLogicalFunctionListElement element = (GsLogicalFunctionListElement)it2.next();
					v.addElement(element.getEdge().getEdge(element.getIndex()));
				}
				if (v.size() > 0) setActivesEdges(v, val);
				GsTreeParam param = new GsTreeParam(exp, v);
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

	public void fireTreeStructureChanged(GsTreeElement element) {
		TreeModelEvent e = new TreeModelEvent(this, new Object[] {element});
		for (Iterator it = treeModelListeners.iterator(); it.hasNext(); ) ((TreeModelListener)it.next()).treeStructureChanged(e);
	}
	public void refreshVertex() {
		boolean dis = false;

		parseFunctions();
		if (node != null) {
			node.setInteractionsModel(this);
			GUIManager.getInstance().getGraphGUI(graph).getNodeEditionPanel().setEditedItem(node);
			for (int p = 0 ; p <= node.getMaxValue(); p++) {
				dis = false;
				for (int k = 0; k < root.getChildCount(); k++)
					if (((GsTreeValue) root.getChild(k)).getValue() == p) {
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
		GsLogicalParameter p;
		GsTreeValue val;
		GsTreeElement exp;
		GsTreeParam param;
		v.clear();

		for (int i = 0; i < root.getChildCount(); i++) {
			val = (GsTreeValue)root.getChild(i);
			for (int j = 0; j < val.getChildCount(); j++) {
				exp = val.getChild(j);
				for (int k = 0; k < exp.getChildCount(); k++) {
					param = (GsTreeParam)exp.getChild(k);
					if (!param.isError()) {
						p = new GsLogicalParameter(val.getValue());
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

	public TreePath getPath(byte v, String e) {
		Object [] path = new Object[3];
		GsTreeValue tval;
		GsTreeElement texp;

		path[0] = root;
		for (int i = 0; i < root.getChildCount(); i++) {
			tval = (GsTreeValue)root.getChild(i);
			if ((byte)tval.getValue() == v) path[1] = tval;
			for (int j = 0; j < tval.getChildCount(); j++) {
				texp = tval.getChild(j);
				if (texp.toString().equals(e)) path[2] = texp;
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
