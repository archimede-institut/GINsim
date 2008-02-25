package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.dnd;

import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.InvalidDnDOperationException;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;

public class GsDragGestureListener implements DragGestureListener {
  private JTree tree;
  private GsDragSourceListener dragSourceListener;
  private GsDropListener dropListener;

  public GsDragGestureListener(JTree tree, GsDragSourceListener dsl, GsDropListener dl) {
    super();
    this.tree = tree;
    dragSourceListener = dsl;
    dropListener = dl;
  }
  public void dragGestureRecognized(DragGestureEvent dge) {
    if (tree.getSelectionCount() > 0) {
      TreePath[] selectedPaths = tree.getSelectionPaths();
      GsTreeElement[] nodes = new GsTreeElement[tree.getSelectionCount()];
      for (int i = 0; i < tree.getSelectionCount(); i++)
        nodes[i] = (GsTreeElement)selectedPaths[i].getLastPathComponent();
      GsTransferable transferable = new GsTransferable(nodes);
      try {
        dragSourceListener.setTransferable(transferable);
        dropListener.setTransferable(transferable);
        dge.startDrag(DragSource.DefaultCopyNoDrop, transferable, dragSourceListener);
      }
      catch (InvalidDnDOperationException e) {
        e.printStackTrace();
      }
    }
  }
}

