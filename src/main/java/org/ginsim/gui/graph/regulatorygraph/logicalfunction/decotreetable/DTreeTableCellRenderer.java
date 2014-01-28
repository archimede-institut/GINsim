package org.ginsim.gui.graph.regulatorygraph.logicalfunction.decotreetable;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


public class DTreeTableCellRenderer implements TableCellRenderer {
	private DecoTree tree;
	
	public DTreeTableCellRenderer(TableTree tree) {
		super();
		this.tree = (DecoTree)tree;
		this.tree.setEditable(true);
	}
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		AbstractDTreeElement ate = (AbstractDTreeElement)value;
		tree.setBackground(ate.getRendererComponent(isSelected).getBackground());
		((TableTree)tree).setVisibleRow(row);
    return tree;
  }
	public DecoTree getTree() {
		return tree;
	}
}
