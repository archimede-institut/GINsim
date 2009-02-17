package fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.decotree;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import java.awt.Component;

public class DTreeCellRenderer extends DefaultTreeCellRenderer {
	public DTreeCellRenderer() {
		super();
	}
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		return ((AbstractDTreeElement)value).getRendererComponent(sel);
	}
}
