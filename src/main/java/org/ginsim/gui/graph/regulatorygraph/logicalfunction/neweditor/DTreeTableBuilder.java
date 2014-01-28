package org.ginsim.gui.graph.regulatorygraph.logicalfunction.neweditor;

import java.awt.Color;
import java.util.Vector;

import org.ginsim.gui.graph.regulatorygraph.logicalfunction.decotreetable.AbstractDTreeElement;
import org.ginsim.gui.graph.regulatorygraph.logicalfunction.decotreetable.DTreeElement;
import org.ginsim.gui.graph.regulatorygraph.logicalfunction.decotreetable.DTreeModel;
import org.ginsim.gui.graph.regulatorygraph.logicalfunction.decotreetable.DecoTreeTable;
import org.ginsim.gui.graph.regulatorygraph.logicalfunction.decotreetable.DecoTreeTableModel;
import org.ginsim.gui.graph.regulatorygraph.logicalfunction.decotreetable.TableDecoTree;


public class DTreeTableBuilder {
	private DTreeModel model;
	private TableDecoTree cr;
	private Vector columns;
	private Vector colWidth;
	private AbstractDTreeElement root, currentNode;
	private DTreeNodeBuilder nb;
	private DecoTreeTable t;

	public DTreeTableBuilder(DTreeNodeBuilder nb) {
		this.nb = nb;
	}
	public void newTree(int h) {
		model = new DTreeModel();
		cr = new TableDecoTree(model, h);
		//DTreeNodeBuilder nb = new DTreeNodeBuilder(true);
		//nb.newNode(cr, title, fg);
		//nb.setNode();
		//currentNode = root = nb.getNode();
		currentNode = root = null;
		columns = new Vector();
		colWidth = new Vector();
	}
	public DTreeElement newNode(String text, Color fg) {
		nb.newNode(cr, text, fg);
		return (DTreeElement)nb.getNode();
	}
	public DTreeTableBuilder addColumn(String title) {
		columns.addElement(title);
		colWidth.addElement(new Integer(0));
		return this;
	}
	public DTreeTableBuilder addColumn(String title, int w) {
		columns.addElement(title);
		colWidth.addElement(new Integer(w));
		return this;
	}
	public void addNode(AbstractDTreeElement node) {
		if (currentNode != null) {
			currentNode.addElement(node);
			node.setParent(currentNode);
		}
		else {
			currentNode = root = node;
		}
		if (!node.isLeaf()) currentNode = node;
	}
	public void decreaseLevel() {
		currentNode = currentNode.getParent();
	}
	public DecoTreeTable getTable() {
		t = new DecoTreeTable(cr);
		t.initModel(root, columns, colWidth);
		return t;
	}
	public void expandtree() {
		int c = cr.getRowCount();
		int i = 0;
		while (i <= c) {
			cr.expandRow(i);
			i++;
			c = cr.getRowCount();
		}
}
	public void clearTree(DecoTreeTable dtt) {
		t = dtt;
		columns = ((DecoTreeTableModel)dtt.getModel()).getColumnNames();
		root = (AbstractDTreeElement)dtt.getTree().getModel().getRoot();
		//String title = root.toString();
		//Color fgc = root.getFgColor();

		model = new DTreeModel();
		cr = (TableDecoTree)dtt.getTree();
		cr.setModel(model);
		//DTreeNodeBuilder nb = new DTreeNodeBuilder(true);
		root.clearChilds();
		nb.setNode(root);
		//nb.newNode(cr, title, fgc);
		//nb.setNode();
		currentNode = root = nb.getNode();
	}
	public void updateTree() {
		t.initModel(root, columns, colWidth);
	}
}
