package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.dnd;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.TreePath;

import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.GsLogicalFunctionTreePanel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeString;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeValue;

public class GsDropListener implements DropTargetListener {
	private GsTransferable transferable;
	private JTree tree;
	private GsLogicalFunctionTreePanel panel;
	private GsTreeElement previousDropable;
	private GsGlassPane glassPane;

	public GsDropListener(GsLogicalFunctionTreePanel panel, GsGlassPane gp) {
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
		GsTreeElement choosenElement;
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
				else if (data instanceof GsTreeElement[]) {
					GsTreeElement[] te = (GsTreeElement[])data;
					tp = tree.getPathForLocation(dtde.getLocation().x, dtde.getLocation().y);
					choosenElement = (GsTreeElement)tp.getLastPathComponent();
					move = dtde.getDropAction() == DnDConstants.ACTION_MOVE;

					//transfert d'une fonction dans une valeur (= changement de valeur pour une fonction)
					if (choosen == GsTransferable.FUNCTION_FLAVOR && choosenElement instanceof GsTreeValue) {
						panel.pasteFunctionsInValue(te, move, (GsTreeValue)choosenElement);
					} else if (choosen == GsTransferable.VALUE_FLAVOR && choosenElement == tree.getPathForRow(0).getLastPathComponent()) {
						panel.pasteValuesInRoot(te, (GsTreeString)choosenElement);
					} else if (choosen == GsTransferable.MANUAL_FLAVOR && choosenElement instanceof GsTreeValue) {
						panel.pasteManualsInValue(te, move, (GsTreeValue)choosenElement);
					} else if (choosen == GsTransferable.PARAM_FLAVOR && choosenElement instanceof GsTreeValue) {
						panel.pasteParamsInValue(te, move, (GsTreeValue)choosenElement);
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
			GsTreeElement choosenElement = (GsTreeElement)tp.getLastPathComponent();
			if (previousDropable != null) {
				previousDropable.setDropable(false);
			}
			if (choosen == GsTransferable.FUNCTION_FLAVOR && choosenElement instanceof GsTreeValue ||
					choosen == GsTransferable.MANUAL_FLAVOR && choosenElement instanceof GsTreeValue ||
					choosen == GsTransferable.PARAM_FLAVOR && choosenElement instanceof GsTreeValue) {
				choosenElement.setDropable(true);
				previousDropable = choosenElement;
				return true;
			}
		}
		return false;
	}
}
