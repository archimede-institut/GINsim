package fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable;

import java.awt.Color;

import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.decotree.AbstractDTreeElement;
import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.decotree.DTreeElement;
import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.decotree.DTreeModel;
import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.decotree.DecoTree;

public class DTreeBuilder {
	protected DTreeNodeBuilder nb;
	protected DTreeModel model;
	protected DecoTree cr;
	protected AbstractDTreeElement root, currentNode;

	public DTreeBuilder(DTreeNodeBuilder nb) {
		this.nb = nb;
	}
	public void newTree(String title, Color fg, int h) {
		model = new DTreeModel();
		cr = new DecoTree(model, h);
		cr.setEditable(true);
		DTreeNodeBuilder nb = new DTreeNodeBuilder(false);
		nb.newNode(cr, title, fg);
		nb.setNode();
		currentNode = root = nb.getNode();
	}
	public void setTree(DecoTree t) {
		cr = t;
		model = (DTreeModel)cr.getModel();
		currentNode = root = (AbstractDTreeElement)model.getRoot();
	}
	public DTreeElement newNode(String text, Color fg) {
		nb.newNode(cr, text, fg);
		return (DTreeElement)nb.getNode();
	}
	public DTreeElement newNode(String text, Color fg, Object uo) {
		nb.newNode(cr, text, fg, uo);
		return (DTreeElement)nb.getNode();
	}
	public void addNode(AbstractDTreeElement node) {
		currentNode.addElement(node);
		node.setParent(currentNode);
		if (!node.isLeaf()) currentNode = node;
	}
	public void decreaseLevel() {
		currentNode = currentNode.getParent();
	}
	public DecoTree getTree() {
		model.init(root, cr);
		model.fireTreeStructureChanged(root);
		return cr;
	}
}
