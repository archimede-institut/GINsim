package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction;

import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.*;

import fr.univmrs.ibdm.GINsim.gui.GsParameterPanel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.*;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.*;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.dnd.*;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeManual;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeParam;
import javax.swing.event.TreeModelEvent;

public class GsLogicalFunctionTreePanel extends GsParameterPanel implements KeyListener {
  private static final long serialVersionUID = -8323666225199589729L;

  class GsTreeUI extends BasicTreeUI {
    protected void paintExpandControl(Graphics g, Rectangle clipBounds, Insets insets,
                                      Rectangle bounds, TreePath path, int row, boolean isExpanded,
                                      boolean hasBeenExpanded, boolean isLeaf) {
      Object value = path.getLastPathComponent();
      if (!isLeaf && (!hasBeenExpanded || treeModel.getChildCount(value) > 0)) {
        int middleXOfKnob = bounds.x - (getRightChildIndent() - 1);
        int middleYOfKnob = bounds.y + 10; //(bounds.height / 2);
        if (isExpanded) {
          Icon expandedIcon = getExpandedIcon();
          if(expandedIcon != null)
            drawCentered(tree, g, expandedIcon, middleXOfKnob, middleYOfKnob );
        }
        else {
          Icon collapsedIcon = getCollapsedIcon();
          if(collapsedIcon != null)
            drawCentered(tree, g, collapsedIcon, middleXOfKnob, middleYOfKnob);
        }
      }
    }
    protected void paintHorizontalPartOfLeg(Graphics g, Rectangle clipBounds, Insets insets,
                                            Rectangle bounds, TreePath path, int row, boolean isExpanded,
                                            boolean hasBeenExpanded, boolean isLeaf) {
      if (!isLeaf) return;
      super.paintHorizontalPartOfLeg(g, clipBounds, insets, bounds, path, row,
                                     isExpanded, hasBeenExpanded, isLeaf);
    }
    protected void paintVerticalPartOfLeg(Graphics g, Rectangle clipBounds, Insets insets, TreePath path) {
      int depth = path.getPathCount() - 1;
      if (depth == 0 && !getShowsRootHandles() && !isRootVisible()) return;
      int lineX = ((depth + 1 + depthOffset) * totalChildIndent) - getRightChildIndent() + insets.left;
      int clipLeft = clipBounds.x;
      int clipRight = clipBounds.x + (clipBounds.width - 1);
      if (((GsTreeElement)path.getLastPathComponent()).getChildCount() == 0) return;
      boolean leaf = ((GsTreeElement)path.getLastPathComponent()).getChild(0).isLeaf();
      if (lineX >= clipLeft && lineX <= clipRight) {
        int clipTop = clipBounds.y;
        int clipBottom = clipBounds.y + clipBounds.height;
        Rectangle parentBounds = getPathBounds(tree, path);
        Rectangle lastChildBounds = getPathBounds(tree, getLastChildPath(path));
        if(lastChildBounds == null) return;

        int top;
        if (parentBounds == null)
          top = Math.max(insets.top + getVerticalLegBuffer(), clipTop);
        else
          top = Math.max(parentBounds.y + parentBounds.height + getVerticalLegBuffer(), clipTop);
        if((depth == 0) && !isRootVisible()) {
          TreeModel model = getModel();
          if(model != null) {
            Object root = model.getRoot();
            if(model.getChildCount(root) > 0) {
              parentBounds = getPathBounds(tree, path.pathByAddingChild(model.getChild(root, 0)));
              if(parentBounds != null)
                top = Math.max(insets.top + getVerticalLegBuffer(), parentBounds.y + parentBounds.height / 2);
            }
          }
        }
        int bottom = Math.min(lastChildBounds.y + (lastChildBounds.height / 2), clipBottom);
        if (!leaf) bottom = Math.min(lastChildBounds.y + 11, clipBottom);
        if (top <= bottom) {
          g.setColor(getHashColor());
          paintVerticalLine(g, tree, lineX, top, bottom);
        }
      }
    }
  }

  private JTree tree;
  private GsTreeInteractionsModel interactionList = null;
  //private GsRegulatoryGraph graph;
  private GsMotionAdapter motionAdapter;
  private GsComponentAdapter componentAdapter;
  private DragSource dragSource;
  private DragGestureListener dragGestureListener;
  private GsDragSourceListener dragSourceListener;
  //private DropTarget dropTarget;
  private GsDropListener dropListener;

  public GsLogicalFunctionTreePanel(GsRegulatoryGraph graph) {
    super();
    setLayout(new BorderLayout());
    add(new JScrollPane(getJTree(graph)), BorderLayout.CENTER);
    this.graph = graph;
    new GsPanelFactory();
  }
  public void setEditedObject(Object obj) {
    GsRegulatoryVertex vertex = (GsRegulatoryVertex)obj;
    interactionList = vertex.getInteractionsModel();
    interactionList.setNode(vertex);
    interactionList.setView(this);
    interactionList.setRootInfos();
    tree.setModel(interactionList);
    repaint();
  }
  private JTree getJTree(GsRegulatoryGraph graph) {
    if (tree == null) {
      interactionList = new GsTreeInteractionsModel(graph);
      tree = new GsJTree(interactionList);
      //tree.setUI(new GsTreeUI());
      tree.setShowsRootHandles(true);
      GsBooleanFunctionTreeRenderer cr = new GsBooleanFunctionTreeRenderer(getPreferredSize().width);
      tree.setCellRenderer(cr);
      tree.setCellEditor(new GsBooleanFunctionTreeEditor(tree, cr));
      tree.setEditable(true);
      tree.addKeyListener(this);
      dragSource = DragSource.getDefaultDragSource();
      dropListener = new GsDropListener(tree, (GsGlassPane)graph.getGraphManager().getMainFrame().getGlassPane());
      dragSourceListener = new GsDragSourceListener(tree, (GsGlassPane)graph.getGraphManager().getMainFrame().getGlassPane());
      dragGestureListener = new GsDragGestureListener(tree, dragSourceListener, dropListener);
      dragSource.createDefaultDragGestureRecognizer(tree, DnDConstants.ACTION_COPY_OR_MOVE, dragGestureListener);
      /*dropTarget =*/ new DropTarget(tree, DnDConstants.ACTION_COPY_OR_MOVE, dropListener, true);
      motionAdapter = new GsMotionAdapter((GsGlassPane)graph.getGraphManager().getMainFrame().getGlassPane());
      tree.addMouseMotionListener(motionAdapter);
      componentAdapter = new GsComponentAdapter((GsGlassPane)graph.getGraphManager().getMainFrame().getGlassPane(), "");
      tree.addMouseListener(componentAdapter);
      addComponentListener(cr);
    }
    return tree ;
  }
  public void refresh() {
    tree.stopEditing();
    Enumeration enu = tree.getExpandedDescendants(tree.getPathForRow(0));
    interactionList.fireTreeStructureChanged((GsTreeElement)interactionList.getRoot());
    if (enu != null)
      while (enu.hasMoreElements()) {
        TreePath tp = (TreePath)enu.nextElement();
        tree.expandPath(tp);
      }
  }
  public void keyPressed(KeyEvent e) {
  }
  public void keyReleased(KeyEvent e) {
    Enumeration enu;
    Vector v;
    GsTreeElement treeElement;

    if (e.getKeyCode() == KeyEvent.VK_DELETE) {
      TreePath[] selectedPaths = tree.getSelectionPaths();
      if (selectedPaths != null) {
        enu = tree.getExpandedDescendants(tree.getPathForRow(0));
        tree.stopEditing();
        v = new Vector();
        tree.stopEditing();
        for (int i = 0; i < selectedPaths.length; i++) {
          treeElement = (GsTreeElement)selectedPaths[i].getLastPathComponent();
          if (treeElement.getParent() instanceof GsTreeManual) {
            treeElement.remove(false);
            v.addElement(treeElement);
          }
          //else if (treeElement.isLeaf())
          //  treeElement.setChecked(false);
          /*else */if (treeElement instanceof GsTreeManual){

          }
          else if (! (treeElement instanceof GsTreeParam)) {
            treeElement.remove(false);
            v.addElement(treeElement);
            if (treeElement.toString().equals(""))
              treeElement.getParent().setProperty("null function", new Boolean(false));
          }
        }
        GsTreeInteractionsModel interactionsModel = (GsTreeInteractionsModel)tree.getModel();
        interactionsModel.refreshVertex();
        interactionsModel.setRootInfos();
        interactionsModel.fireTreeStructureChanged((GsTreeElement)interactionsModel.getRoot());
        while (enu.hasMoreElements()) {
          TreePath tp = (TreePath)enu.nextElement();
          if (!v.contains(tp.getLastPathComponent())) tree.expandPath(tp);
        }
      }
    }
  }
  public void keyTyped(KeyEvent e) {
  }
}
