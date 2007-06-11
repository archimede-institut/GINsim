package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.*;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.*;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.GsTreeInteractionsModel;
import java.util.Enumeration;

public class GsDropListener implements DropTargetListener {
  private GsTransferable transferable;
  private JTree tree;
  private GsTreeElement previousDropable;

  public GsDropListener(JTree tree) {
    this.tree = tree;
    previousDropable = null;
  }

  public void dragDropEnd(DragSourceDropEvent dsde) {
  }

  public void setTransferable(GsTransferable t) {
    transferable = t;
  }

  public void dragEnter(DropTargetDragEvent dtde) {
    if (! isDragEnabled(dtde))
      dtde.rejectDrag();
    else {
      dtde.acceptDrag(dtde.getDropAction());
    }
  }

  public void dragExit(DropTargetEvent dte) {
   if (previousDropable != null) previousDropable.setDropable(false);
    previousDropable = null;
  }

  public void dragOver(DropTargetDragEvent dtde) {
    if (! isDragEnabled(dtde))
      dtde.rejectDrag();
    else {
      dtde.acceptDrag(dtde.getDropAction());
    }

  }

  public void dropActionChanged(DropTargetDragEvent dtde) {
  }

  public void drop(DropTargetDropEvent dtde) {
    DataFlavor choosen = null;

    if (!dtde.isLocalTransfer())
      choosen = GsTransferable.PLAIN_TEXT_FLAVOR;
    else if (dtde.isDataFlavorSupported(transferable.getCurrentFlavor()))
      choosen = transferable.getCurrentFlavor();
    if (choosen != null) {
      try {
        dtde.acceptDrop(dtde.getDropAction());
        Object data = transferable.getTransferData(choosen);
        if (data == null) {
          dtde.dropComplete(false);
          throw new NullPointerException();
        }
        else {
          if (data instanceof GsTreeElement[]) {
            GsTreeElement[] te = (GsTreeElement[])data;
            TreePath tp = tree.getPathForLocation(dtde.getLocation().x, dtde.getLocation().y);
            GsTreeElement choosenElement = (GsTreeElement)tp.getLastPathComponent();
            for (int i = 0; i < te.length; i++) {
              // transfert d'un parametre dans une fonction (= inactivation d'un parametre)
              if ((choosen == GsTransferable.PARAM_FLAVOR) && (choosenElement instanceof GsTreeExpression)) {
                te[i].setChecked(false);
                if (tree != null) {
                  Enumeration enu = tree.getExpandedDescendants(tree.getPathForRow(0));
                  ((GsTreeInteractionsModel)tree.getModel()).refreshVertex();
                  tree.stopEditing();
                  ((GsTreeInteractionsModel)tree.getModel()).fireTreeStructureChanged(te[i].getParent());
                  while (enu.hasMoreElements()) tree.expandPath((TreePath)enu.nextElement());
                }
              }
              //transfert d'une fonction dans une valeur (= changement de valeur pour une fonction)
              else if ((choosen == GsTransferable.FUNCTION_FLAVOR) && (choosenElement instanceof GsTreeValue)) {
                try {
                  ((GsTreeInteractionsModel)tree.getModel()).addExpression(
                    tree,
                    (short)((GsTreeValue)choosenElement).getValue(),
                    ((GsTreeInteractionsModel)tree.getModel()).getVertex(),
                    te[i].toString());
                  if (dtde.getDropAction() == DnDConstants.ACTION_MOVE) te[i].remove();
                  ((GsTreeInteractionsModel)tree.getModel()).removeNullFunction((short)((GsTreeValue)choosenElement).getValue());
                  ((GsTreeInteractionsModel)tree.getModel()).fireTreeStructureChanged(choosenElement);
                  ((GsTreeInteractionsModel)tree.getModel()).refreshVertex();
                }
                catch (Exception ex) {
                  ex.printStackTrace();
                }
              }
              // transfert d'une valeur dans la racine (= simple reorganisation des valeurs)
              else if ((choosen == GsTransferable.VALUE_FLAVOR) && (choosenElement == tree.getPathForRow(0).getLastPathComponent())) {

              }
            }
          }
          else
            dtde.rejectDrop();
          if (previousDropable != null) previousDropable.setDropable(false);
        }
      }
      catch (Throwable t) {
        t.printStackTrace();
        dtde.dropComplete(false);
      }
    }
    else
      dtde.rejectDrop();
  }

  private boolean isDragEnabled(DropTargetDragEvent dtde) {
    DataFlavor choosen = transferable.getCurrentFlavor();
    if (choosen != null) {
      if (choosen == GsTransferable.MIXED_FLAVOR) return false;
      TreePath tp = tree.getPathForLocation(dtde.getLocation().x, dtde.getLocation().y);
      if (tp == null) return false;
      GsTreeElement choosenElement = (GsTreeElement)tp.getLastPathComponent();
      if (previousDropable != null) previousDropable.setDropable(false);
      if (((choosen == GsTransferable.FUNCTION_FLAVOR) && (choosenElement instanceof GsTreeValue)) ||
          ((choosen == GsTransferable.VALUE_FLAVOR) && (choosenElement instanceof GsTreeString)) ||
          ((choosen == GsTransferable.PARAM_FLAVOR) && (choosenElement == transferable.getPParent()))) {
        choosenElement.setDropable(true);
        previousDropable = choosenElement;
        return true;
      }
    }
    return false;
  }
}
