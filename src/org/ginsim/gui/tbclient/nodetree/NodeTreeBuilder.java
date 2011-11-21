package org.ginsim.gui.tbclient.nodetree;

import java.awt.Color;

import org.ginsim.gui.tbclient.decotreetable.DTreeBuilder;
import org.ginsim.gui.tbclient.decotreetable.DTreeNodeBuilder;
import org.ginsim.gui.tbclient.decotreetable.decotree.AbstractDTreeElement;
import org.ginsim.gui.tbclient.decotreetable.decotree.DecoTree;


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
