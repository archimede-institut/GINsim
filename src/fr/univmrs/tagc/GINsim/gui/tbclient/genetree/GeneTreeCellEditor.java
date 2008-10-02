package fr.univmrs.tagc.GINsim.gui.tbclient.genetree;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;

public class GeneTreeCellEditor extends DefaultTreeCellEditor {

	public GeneTreeCellEditor(JTree tree, DefaultTreeCellRenderer renderer) {
		super(tree, renderer);
	}
	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean isSelected, boolean expanded,
			 boolean leaf, int row) {
		return renderer.getTreeCellRendererComponent(tree, value, true, expanded, leaf, row, true);
	}
	protected boolean canEditImmediately(EventObject event) {
		if ((event instanceof MouseEvent) && SwingUtilities.isLeftMouseButton((MouseEvent)event)) {
			MouseEvent me = (MouseEvent)event;
      return ((me.getClickCount() == 1) && inHitRegion(me.getX(), me.getY()));
		}
		return (event == null);
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
