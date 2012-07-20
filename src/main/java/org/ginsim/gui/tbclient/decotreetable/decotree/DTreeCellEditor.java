package org.ginsim.gui.tbclient.decotreetable.decotree;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;

public class DTreeCellEditor extends DefaultTreeCellEditor {

	public DTreeCellEditor(JTree tree, DefaultTreeCellRenderer renderer) {
		super(tree, renderer);
  }
	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded, boolean leaf, int row) {
		AbstractDTreeElement ate = (AbstractDTreeElement)value;
		return ate.getEditorComponent(true);
	}
	protected boolean canEditImmediately(EventObject event) {
		boolean ok = false;
		if ((event instanceof MouseEvent) && SwingUtilities.isLeftMouseButton((MouseEvent)event)) {
			MouseEvent me = (MouseEvent)event;
			ok = ((me.getClickCount() == 1) && inHitRegion(me.getX(), me.getY()));
		}
		else
			ok = (event == null);
		return ok;
	}

	protected boolean inHitRegion(int x, int y) {
		if (lastRow != -1 && tree != null) {
			Rectangle bounds = tree.getRowBounds(lastRow);
			ComponentOrientation treeOrientation = tree.getComponentOrientation();

			if ( treeOrientation.isLeftToRight() ) {
				if (bounds != null && x <= (bounds.x) && offset < (bounds.width - 5)) {
					return false;
				}
			}
			else if (bounds != null && ( x >= (bounds.x+bounds.width-offset+5) || x <= (bounds.x + 5) ) && offset < (bounds.width - 5) ) {
				return false;
			}
		}
		return true;
	}
}
