package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction;

import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.plaf.basic.BasicTreeUI;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import fr.univmrs.ibdm.GINsim.graph.GsGraphNotificationMessage;
import fr.univmrs.ibdm.GINsim.gui.GsParameterPanel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.GsBooleanFunctionTreeEditor;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.GsBooleanFunctionTreeRenderer;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.GsTreeInteractionsModel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.datamodel.GsTreeElement;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.dnd.GsGlassPane;
import javax.swing.JRootPane;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.util.Enumeration;
import java.util.Vector;

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
  private GsRegulatoryGraph graph;
  private GsGlassPane glassPane = new GsGlassPane();
  private JRootPane rootPane;

  public GsLogicalFunctionTreePanel(GsRegulatoryGraph graph) {
    super();
    setLayout(new BorderLayout());
    rootPane = new JRootPane();
    rootPane.getContentPane().add(new JScrollPane(getJTree(graph)));
    rootPane.setGlassPane(glassPane);
    glassPane.setVisible(true);
    add(rootPane, BorderLayout.CENTER);
    this.graph = graph;
  }
  public JRootPane getRootPane() {
    return rootPane;
  }
  public void setEditedObject(Object obj) {
    GsRegulatoryVertex vertex = (GsRegulatoryVertex)obj;
    interactionList = vertex.getInteractionsModel();
    interactionList.setNode(vertex);
    tree.setModel(interactionList);
    interactionList.setGlassPane(glassPane);
    repaint();
  }
  private JTree getJTree(GsRegulatoryGraph graph) {
    if (tree == null) {
      interactionList = new GsTreeInteractionsModel(graph, glassPane);
      tree = new JTree(interactionList);
      tree.setUI(new GsTreeUI());
      tree.setShowsRootHandles(true);
      GsBooleanFunctionTreeRenderer cr = new GsBooleanFunctionTreeRenderer(getPreferredSize().width);
      tree.setCellRenderer(cr);
      tree.setCellEditor(new GsBooleanFunctionTreeEditor(tree, cr));
      tree.setEditable(true);
      tree.addKeyListener(this);
      addComponentListener(cr);
    }
    return tree;
  }
  public void addExpression(short val, GsRegulatoryVertex currentVertex, String expression) throws Exception {
    GsBooleanParser tbp = new GsBooleanParser(graph.getGraphManager().getIncomingEdges(currentVertex));
    if (!tbp.compile(expression)) {
      graph.addNotificationMessage(new GsGraphNotificationMessage(graph, "invalid formula",
        GsGraphNotificationMessage.NOTIFICATION_WARNING));
    }
    else {
      tree.stopEditing();
      interactionList.addExpression(val, currentVertex, tbp);
      ((GsTreeInteractionsModel)tree.getModel()).fireTreeStructureChanged((GsTreeElement)tree.getModel().getRoot());
      tree.expandPath(interactionList.getPath(val, tbp.getRoot().toString()));
      graph.getVertexAttributePanel().setEditedObject(currentVertex);
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
