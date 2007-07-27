package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.GsTreeInteractionsModel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeExpression;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeString;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeValue;
import javax.swing.SwingUtilities;
import java.awt.Point;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeManual;
import java.util.Vector;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeParam;

public class GsDropListener implements DropTargetListener {
  private GsTransferable transferable;
  private JTree tree;
  private GsTreeElement previousDropable;
  private GsGlassPane glassPane;

  public GsDropListener(JTree tree, GsGlassPane gp) {
    this.tree = tree;
    previousDropable = null;
    glassPane = gp;
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
    Point p = (Point)dtde.getLocation().clone();
    SwingUtilities.convertPointToScreen(p, tree);
    SwingUtilities.convertPointFromScreen(p, glassPane);
    glassPane.setPoint(p);
    glassPane.repaint();
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
    GsTreeInteractionsModel interactionsModel = null;
    Object data = null;
    TreePath tp;
    GsTreeElement choosenElement;
    Enumeration enu_expanded, enu;
    GsTreeParam param;

    if (tree != null) {
      tree.stopEditing();
      interactionsModel = (GsTreeInteractionsModel)tree.getModel();
    }
    if (!dtde.isLocalTransfer())
      choosen = GsTransferable.PLAIN_TEXT_FLAVOR;
    else if (dtde.isDataFlavorSupported(transferable.getCurrentFlavor()))
      choosen = transferable.getCurrentFlavor();
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
          for (int i = 0; i < te.length; i++) {
            // transfert d'un parametre dans une fonction (= inactivation d'un parametre)
            //if ((choosen == GsTransferable.PARAM_FLAVOR) && (choosenElement instanceof GsTreeExpression)) {
            //te[i].setChecked(false);
            //  if (tree != null) {
            //    Enumeration enu = tree.getExpandedDescendants(tree.getPathForRow(0));
            //    interactionsModel.refreshVertex();
            //    tree.stopEditing();
            //    interactionsModel.fireTreeStructureChanged(te[i].getParent());
            //   while (enu.hasMoreElements()) tree.expandPath((TreePath)enu.nextElement());
            //  }
            //}
            //transfert d'une fonction dans une valeur (= changement de valeur pour une fonction)
            /*else*/
            if ((choosen == GsTransferable.FUNCTION_FLAVOR) && (choosenElement instanceof GsTreeValue)) {
              try {
                if (((GsTreeValue)te[i].getParent()).getValue() != ((GsTreeValue)choosenElement).getValue()) {
                  enu_expanded = tree.getExpandedDescendants(tree.getPathForRow(0));
                  interactionsModel.addExpression(tree, (short)((GsTreeValue)choosenElement).getValue(),
                                                  interactionsModel.getVertex(), te[i].toString());
                  if (dtde.getDropAction() == DnDConstants.ACTION_MOVE) te[i].remove(false);
                  interactionsModel.removeNullFunction((short)((GsTreeValue)choosenElement).getValue());
                  interactionsModel.fireTreeStructureChanged((GsTreeElement)tree.getPathForRow(0).
                    getLastPathComponent());
                  interactionsModel.refreshVertex();
                  while (enu_expanded.hasMoreElements()) {
                    tp = (TreePath)enu_expanded.nextElement();
                    tree.expandPath(tp);
                  }
                }
              }
              catch (Exception ex) {
                ex.printStackTrace();
              }
            }
            else if ((choosen == GsTransferable.FUNCTION_FLAVOR) && (choosenElement instanceof GsTreeManual)) {
              enu_expanded = tree.getExpandedDescendants(tree.getPathForRow(0));
              enu = te[i].getChilds().elements();
              while (enu.hasMoreElements()) {
                param = (GsTreeParam)enu.nextElement();
                ((GsTreeManual)choosenElement).addChild(new GsTreeParam(choosenElement, param.getEdgeIndexes()));
              }
              if (dtde.getDropAction() == DnDConstants.ACTION_MOVE) te[i].remove(false);
              interactionsModel.fireTreeStructureChanged((GsTreeElement) tree.getPathForRow(0).getLastPathComponent());
              interactionsModel.refreshVertex();
              while (enu_expanded.hasMoreElements()) {
                tp = (TreePath)enu_expanded.nextElement();
                tree.expandPath(tp);
              }
            }
            else if ((choosen == GsTransferable.MANUAL_FLAVOR) && (choosenElement instanceof GsTreeValue)) {
              if (((GsTreeValue)choosenElement).getValue() != ((GsTreeValue)te[i].getParent()).getValue()) {
                enu_expanded = tree.getExpandedDescendants(tree.getPathForRow(0));
                enu = te[i].getChilds().elements();
                while (enu.hasMoreElements()) {
                  param = (GsTreeParam)enu.nextElement();
                  choosenElement.getChild(0).addChild(new GsTreeParam(choosenElement.getChild(0),
                    param.getEdgeIndexes()));
                }
                if (dtde.getDropAction() == DnDConstants.ACTION_MOVE)te[i].clearChilds();
                interactionsModel.fireTreeStructureChanged((GsTreeElement)tree.getPathForRow(0).
                  getLastPathComponent());
                interactionsModel.refreshVertex();
                while (enu_expanded.hasMoreElements()) {
                  tp = (TreePath)enu_expanded.nextElement();
                  tree.expandPath(tp);
                }
              }
            }
            else if ((choosen == GsTransferable.MANUAL_FLAVOR) && (choosenElement instanceof GsTreeManual)) {
              if (choosenElement != te[i]) {
                enu_expanded = tree.getExpandedDescendants(tree.getPathForRow(0));
                enu = te[i].getChilds().elements();
                while (enu.hasMoreElements()) {
                  param = (GsTreeParam)enu.nextElement();
                  choosenElement.addChild(new GsTreeParam(choosenElement, param.getEdgeIndexes()));
                }
                if (dtde.getDropAction() == DnDConstants.ACTION_MOVE) te[i].clearChilds();
                interactionsModel.fireTreeStructureChanged((GsTreeElement)tree.getPathForRow(0).
                  getLastPathComponent());
                interactionsModel.refreshVertex();
                while (enu_expanded.hasMoreElements()) {
                  tp = (TreePath)enu_expanded.nextElement();
                  tree.expandPath(tp);
                }
              }
            }
            else if ((choosen == GsTransferable.PARAM_FLAVOR) && (choosenElement instanceof GsTreeValue)) {
              enu_expanded = tree.getExpandedDescendants(tree.getPathForRow(0));
              choosenElement.getChild(0).addChild(new GsTreeParam(choosenElement.getChild(0),
                ((GsTreeParam)te[i]).getEdgeIndexes()));
              if (dtde.getDropAction() == DnDConstants.ACTION_MOVE) te[i].remove(false);
              interactionsModel.fireTreeStructureChanged((GsTreeElement)tree.getPathForRow(0).getLastPathComponent());
              interactionsModel.refreshVertex();
              while (enu_expanded.hasMoreElements()) {
                tp = (TreePath)enu_expanded.nextElement();
                tree.expandPath(tp);
              }
            }
            else if ((choosen == GsTransferable.PARAM_FLAVOR) && (choosenElement instanceof GsTreeManual)) {
              enu_expanded = tree.getExpandedDescendants(tree.getPathForRow(0));
              choosenElement.addChild(new GsTreeParam(choosenElement, ((GsTreeParam)te[i]).getEdgeIndexes()));
              if (dtde.getDropAction() == DnDConstants.ACTION_MOVE) te[i].remove(false);
              interactionsModel.fireTreeStructureChanged((GsTreeElement)tree.getPathForRow(0).getLastPathComponent());
              interactionsModel.refreshVertex();
              while (enu_expanded.hasMoreElements()) {
                tp = (TreePath)enu_expanded.nextElement();
                tree.expandPath(tp);
              }
            }
            // transfert d'une valeur dans la racine (= simple reorganisation des valeurs)
            //else if ((choosen == GsTransferable.VALUE_FLAVOR) && (choosenElement == tree.getPathForRow(0).getLastPathComponent())) {

            //}
            //}
            else
              dtde.rejectDrop();
            if (previousDropable != null) previousDropable.setDropable(false);
          }
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
          ((choosen == GsTransferable.FUNCTION_FLAVOR) && (choosenElement instanceof GsTreeManual)) ||
          ((choosen == GsTransferable.MANUAL_FLAVOR) && (choosenElement instanceof GsTreeValue)) ||
          ((choosen == GsTransferable.MANUAL_FLAVOR) && (choosenElement instanceof GsTreeManual)) ||
          ((choosen == GsTransferable.PARAM_FLAVOR) && (choosenElement instanceof GsTreeManual)) ||
          ((choosen == GsTransferable.PARAM_FLAVOR) && (choosenElement instanceof GsTreeValue))) {
        choosenElement.setDropable(true);
        previousDropable = choosenElement;
        return true;
      }
    }
    return false;
  }
}
