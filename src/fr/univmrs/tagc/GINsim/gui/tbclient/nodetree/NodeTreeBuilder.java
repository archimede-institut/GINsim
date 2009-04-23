package fr.univmrs.tagc.GINsim.gui.tbclient.nodetree;

import java.awt.Color;

import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.DTreeBuilder;
import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.DTreeNodeBuilder;
import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.decotree.AbstractDTreeElement;
import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.decotree.DecoTree;

public class NodeTreeBuilder extends DTreeBuilder {
	public NodeTreeBuilder(DTreeNodeBuilder nb) {
		super(nb);
	}
	public void newTree(String title, Color fg, int h) {
		model = new NodeTreeModel();
		cr = new DecoTree(model, h);
		cr.setEditable(true);
		DTreeNodeBuilder nb = new DTreeNodeBuilder(false);
		nb.newNode(cr, title, fg);
		nb.setNode();
		currentNode = root = nb.getNode();
	}
	public void setTree(DecoTree t) {
		cr = t;
		model = (NodeTreeModel)cr.getModel();
		currentNode = root = (AbstractDTreeElement)model.getRoot();
	}
	public void setCurrentNode(AbstractDTreeElement cn) {
		currentNode = cn;
		
	}
	
}
