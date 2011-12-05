package org.ginsim.gui.graph.regulatorygraph.logicalfunction.graphictree.dnd;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.datamodel.TreeElement;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.datamodel.TreeExpression;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.datamodel.TreeString;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.datamodel.TreeValue;
import org.ginsim.gui.graph.regulatorygraph.logicalfunction.LogicalFunctionTreePanel;


public class DropListener implements DropTargetListener {
	private GsTransferable transferable;
	private JTree tree;
	private LogicalFunctionTreePanel panel;
	private TreeElement previousDropable;
	private GlassPane glassPane;

	public DropListener(LogicalFunctionTreePanel panel, GlassPane gp) {
		tree = panel.getTree();
		this.panel = panel;
		previousDropable = null;
		glassPane = gp;
	}

	public void dragDropEnd(DragSourceDropEvent dsde) {
	}

	public void setTransferable(GsTransferable t) {
		transferable = t;
	}

	public void dragEnter(DropTargetDragEvent dtde) {
		if (! isDragEnabled(dtde)) {
			dtde.rejectDrag();
		} else {
			dtde.acceptDrag(dtde.getDropAction());
		}
	}

	public void dragExit(DropTargetEvent dte) {
		if (previousDropable != null) {
			previousDropable.setDropable(false);
		}
		previousDropable = null;
	}

	public void dragOver(DropTargetDragEvent dtde) {
		Point p = (Point)dtde.getLocation().clone();
		SwingUtilities.convertPointToScreen(p, tree);
		SwingUtilities.convertPointFromScreen(p, glassPane);
		glassPane.setPoint(p);
		glassPane.repaint();
		if (! isDragEnabled(dtde)) {
			dtde.rejectDrag();
		} else {
			dtde.acceptDrag(dtde.getDropAction());
		}
	}

	public void dropActionChanged(DropTargetDragEvent dtde) {
	}

	public void drop(DropTargetDropEvent dtde) {
		DataFlavor choosen = null;
		Object data = null;
		TreePath tp;
		TreeElement choosenElement;
		boolean move;

		if (tree != null) {
			tree.stopEditing();
		}
		if (!dtde.isLocalTransfer()) {
			choosen = GsTransferable.PLAIN_TEXT_FLAVOR;
		} else if (dtde.isDataFlavorSupported(transferable.getCurrentFlavor())) {
			choosen = transferable.getCurrentFlavor();
		}
		if (choosen != null) {
			try {
				dtde.acceptDrop(dtde.getDropAction());
				data = transferable.getTransferData(choosen);
				if (data == null) {
					dtde.dropComplete(false);
					throw new NullPointerException();
				}
				else if (data instanceof TreeElement[]) {
					TreeElement[] te = (TreeElement[])data;
					tp = tree.getPathForLocation(dtde.getLocation().x, dtde.getLocation().y);
					choosenElement = (TreeElement)tp.getLastPathComponent();
					move = dtde.getDropAction() == DnDConstants.ACTION_MOVE;

					//transfert d'une fonction dans une valeur (= changement de valeur pour une fonction)
					if (choosen == GsTransferable.FUNCTION_FLAVOR && choosenElement instanceof TreeValue) {
						panel.pasteFunctionsInValue(te, move, (TreeValue)choosenElement);
					}
					else if (choosen == GsTransferable.VALUE_FLAVOR && choosenElement == tree.getPathForRow(0).getLastPathComponent()) {
						panel.pasteValuesInRoot(te, (TreeString)choosenElement);
					}
					/*else if (choosen == GsTransferable.PARAM_FLAVOR && choosenElement instanceof TreeValue) {
						panel.pasteParamsInValue(te, move, (TreeValue)choosenElement);
					}*/
				  else if (choosen == GsTransferable.FUNCTION_FLAVOR && choosenElement instanceof TreeExpression) {
						panel.pasteExpressionsInExpression(te, move, (TreeExpression)choosenElement);
					}
					if (previousDropable != null) {
						previousDropable.setDropable(false);
					}
				}
			}
			catch (Throwable t) {
				t.printStackTrace();
				dtde.dropComplete(false);
			}
		} else {
			dtde.rejectDrop();
		}
	}

	private boolean isDragEnabled(DropTargetDragEvent dtde) {
		DataFlavor choosen = transferable.getCurrentFlavor();
		if (choosen != null) {
			if (choosen == GsTransferable.MIXED_FLAVOR) {
				return false;
			}
			TreePath tp = tree.getPathForLocation(dtde.getLocation().x, dtde.getLocation().y);
			if (tp == null) {
				return false;
			}
			TreeElement choosenElement = (TreeElement)tp.getLastPathComponent();
			if (previousDropable != null) {
				previousDropable.setDropable(false);
			}
			if (choosen == GsTransferable.FUNCTION_FLAVOR && choosenElement instanceof TreeValue) {
				choosenElement.setDropable(true);
				previousDropable = choosenElement;
				return true;
			}
			else if (choosen == GsTransferable.FUNCTION_FLAVOR && choosenElement instanceof TreeExpression) {
				try {
					int choosenValue = ((TreeValue) choosenElement.getParent()).getValue();
					TreeElement[] dragElements = (TreeElement[])transferable.getTransferData(choosen);
					int dragValue = ((TreeValue)dragElements[0].getParent()).getValue();
					boolean ok = true;
					for (int i = 0; i < dragElements.length; i++)
						if (dragElements[i] == choosenElement) {
							ok = false;
							break;
						}
					if ((choosenValue == dragValue) && ok) {
						choosenElement.setDropable(true);
						previousDropable = choosenElement;
						return true;
					}
				}
				catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}
		return false;
	}
}
