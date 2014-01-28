package org.ginsim.gui.graph.regulatorygraph.logicalfunction.graphictree.dnd;

import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.InvalidDnDOperationException;

import javax.swing.JTree;
import javax.swing.tree.TreePath;

import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.TreeElement;


public class GsDragGestureListener implements DragGestureListener {
  private JTree tree;
  private GsDragSourceListener dragSourceListener;
  private DropListener dropListener;

  public GsDragGestureListener(JTree tree, GsDragSourceListener dsl, DropListener dl) {
    super();
    this.tree = tree;
    dragSourceListener = dsl;
    dropListener = dl;
  }
  public void dragGestureRecognized(DragGestureEvent dge) {
    if (tree.getSelectionCount() > 0) {
      TreePath[] selectedPaths = tree.getSelectionPaths();
      TreeElement[] nodes = new TreeElement[tree.getSelectionCount()];
      for (int i = 0; i < tree.getSelectionCount(); i++)
        nodes[i] = (TreeElement)selectedPaths[i].getLastPathComponent();
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

