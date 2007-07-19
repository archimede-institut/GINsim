package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import fr.univmrs.ibdm.GINsim.gui.GsParameterPanel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.GsBooleanFunctionTreeEditor;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.GsBooleanFunctionTreeRenderer;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.GsJTree;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.GsPanelFactory;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.GsTreeInteractionsModel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.dnd.GsComponentAdapter;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.dnd.GsDragGestureListener;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.dnd.GsDragSourceListener;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.dnd.GsDropListener;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.dnd.GsGlassPane;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.dnd.GsMotionAdapter;

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
  class DropManager implements DropTargetListener {
    public void dragEnter(DropTargetDragEvent dtde) {
      System.err.println("dragEnter : " + dtde.getLocation());
    }
    public void dragExit(DropTargetEvent dtde) {
      System.err.println("dragExit : ");
    }
    public void dragOver(DropTargetDragEvent dtde) {
      System.err.println("dragOver : " + dtde.getLocation());
      //motionAdapter.mouseDragged(new MouseEvent(
      //  tree, MouseEvent.MOUSE_DRAGGED, 0, 0, dtde.getLocation().x, dtde.getLocation().y, 1, false));
    }
    public void drop(DropTargetDropEvent dtde) {
      System.err.println("drop : " + dtde.getLocation());
      //componentAdapter.mouseReleased(new MouseEvent(
      //  tree, MouseEvent.MOUSE_RELEASED, 0, 0, dtde.getLocation().x, dtde.getLocation().y, 1, false));
    }
    public void dropActionChanged(DropTargetDragEvent dtde) {
      System.err.println("dropActionChanged : " + dtde.getLocation());
    }
  }
  private JTree tree;
  private GsTreeInteractionsModel interactionList = null;
  private GsRegulatoryGraph graph;
  private GsMotionAdapter motionAdapter;
  private GsComponentAdapter componentAdapter;
  private DragSource dragSource;
  private DragGestureListener dragGestureListener;
  private GsDragSourceListener dragSourceListener;
  private DropTarget dropTarget;
  private GsDropListener dropListener;

  public GsLogicalFunctionTreePanel(GsRegulatoryGraph graph) {
    super();
    setLayout(new BorderLayout());
    add(new JScrollPane(getJTree(graph)), BorderLayout.CENTER);
    this.graph = graph;
    new GsPanelFactory(graph.getGraphManager().getMainFrame().getGlassPane());
  }
  public void setEditedObject(Object obj) {
    GsRegulatoryVertex vertex = (GsRegulatoryVertex)obj;
    interactionList = vertex.getInteractionsModel();
    interactionList.setNode(vertex);
    tree.setModel(interactionList);
    repaint();
  }
  private JTree getJTree(GsRegulatoryGraph graph) {
    if (tree == null) {
      interactionList = new GsTreeInteractionsModel(graph);
      tree = new GsJTree(interactionList);
      tree.setUI(new GsTreeUI());
      tree.setShowsRootHandles(true);
      GsBooleanFunctionTreeRenderer cr = new GsBooleanFunctionTreeRenderer(getPreferredSize().width);
      tree.setCellRenderer(cr);
      tree.setCellEditor(new GsBooleanFunctionTreeEditor(tree, cr));
      tree.setEditable(true);
      tree.addKeyListener(this);
      dragSource = DragSource.getDefaultDragSource();
      dropListener = new GsDropListener(tree);
      dragSourceListener = new GsDragSourceListener(tree, (GsGlassPane)graph.getGraphManager().getMainFrame().getGlassPane());
      dragGestureListener = new GsDragGestureListener(tree, dragSourceListener, dropListener);
      dragSource.createDefaultDragGestureRecognizer(tree, DnDConstants.ACTION_COPY_OR_MOVE, dragGestureListener);
      dropTarget = new DropTarget(tree, DnDConstants.ACTION_COPY_OR_MOVE, dropListener, true);
      motionAdapter = new GsMotionAdapter((GsGlassPane)graph.getGraphManager().getMainFrame().getGlassPane());
      tree.addMouseMotionListener(motionAdapter);
      componentAdapter = new GsComponentAdapter((GsGlassPane)graph.getGraphManager().getMainFrame().getGlassPane(), "");
      tree.addMouseListener(componentAdapter);
      addComponentListener(cr);
    }
    return tree;
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
          if (treeElement.isLeaf())
            treeElement.setChecked(false);
          else {
            treeElement.remove();
            v.addElement(treeElement);
            if (treeElement.toString().equals(""))
              treeElement.getParent().setProperty("null function", new Boolean(false));
          }
        }
        ((GsTreeInteractionsModel)tree.getModel()).fireTreeStructureChanged((GsTreeElement)tree.getModel().getRoot());
        ((GsTreeInteractionsModel)tree.getModel()).refreshVertex();
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
