package fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.table;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.*;
import javax.swing.tree.TreePath;

import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.decotree.AbstractDTreeElement;
import fr.univmrs.tagc.GINsim.gui.tbclient.decotreetable.decotree.DTreePanel;

import java.awt.Rectangle;

public class DTreeTableCellEditor extends DefaultCellEditor {
	private static final long serialVersionUID = 4908136333623167309L;
	protected int visibleRow;
	private DTreeTableCellRenderer treeTableRenderer;

	public DTreeTableCellEditor(DTreeTableCellRenderer treeRenderer) {
		super(new JTextField());
		this.treeTableRenderer = treeRenderer;
		setClickCountToStart(1);
	}
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		Rectangle bounds = treeTableRenderer.getTree().getRowBounds(row);
		int offset = bounds.x;
		DTreePanel c = (DTreePanel)((AbstractDTreeElement)value).getEditorComponent(isSelected);
		c.addComponent(Box.createHorizontalStrut(offset), 0, 0, 1, 1, 0.0, 0.0, DTreePanel.WEST, DTreePanel.NONE, 0, 0, 0, 0, 0, 0);
		c.setOpaque(false);
		return c;
	}
	public boolean isCellEditable(EventObject e) {
		if (e instanceof MouseEvent) {
			MouseEvent me = (MouseEvent)e;
			TreePath tp = treeTableRenderer.getTree().getPathForLocation(me.getX(), me.getY());
			if (tp != null) {
				AbstractDTreeElement el = (AbstractDTreeElement)tp.getLastPathComponent();
				Rectangle bounds = treeTableRenderer.getTree().getPathBounds(tp);
				if ((me.getX() < bounds.x) || (me.getX() > (bounds.width + bounds.x))) {
					treeTableRenderer.getTree().dispatchEvent(me);
					return false;
				}
				if (!el.isEditable()) treeTableRenderer.getTree().dispatchEvent(me);
				return ((me.getClickCount() == 1) && (el.isEditable()));
			}
			else {
				treeTableRenderer.getTree().dispatchEvent(me);
				return false;
			}
		}
		return false;
	}
}
