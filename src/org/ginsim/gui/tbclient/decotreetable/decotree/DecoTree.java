package org.ginsim.gui.tbclient.decotreetable.decotree;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;


public class DecoTree extends JTree {
	private static final long serialVersionUID = 1683057279195963727L;
	public DecoTree(TreeModel model, int h) {
		super(model);
		DTreeCellRenderer r = new DTreeCellRenderer();
		setCellRenderer(r);
		DTreeCellEditor cellEditor = new DTreeCellEditor(this, r);
		setCellEditor(cellEditor);
		setShowsRootHandles(true);
		setRowHeight(h);
		setEditable(true);
	}
}
