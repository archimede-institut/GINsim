package fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.decotree;

import javax.swing.JTree;
import javax.swing.tree.TreeModel;


public class DecoTree extends JTree {
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
