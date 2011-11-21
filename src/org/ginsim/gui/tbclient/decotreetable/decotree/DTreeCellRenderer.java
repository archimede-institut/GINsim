package org.ginsim.gui.tbclient.decotreetable.decotree;

import java.awt.Component;

import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

public class DTreeCellRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = -3839683140995761773L;
	public DTreeCellRenderer() {
		super();
	}
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		return ((AbstractDTreeElement)value).getRendererComponent(sel);
	}
}
