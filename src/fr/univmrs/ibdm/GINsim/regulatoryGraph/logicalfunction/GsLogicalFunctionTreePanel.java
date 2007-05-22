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

public class GsLogicalFunctionTreePanel extends GsParameterPanel {
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
      int lineX = getRowX(-1, depth + 1) - getRightChildIndent() + insets.left;
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

  public GsLogicalFunctionTreePanel(GsRegulatoryGraph graph) {
    super();
    setLayout(new BorderLayout());
    add(new JScrollPane(getJTree(graph)), BorderLayout.CENTER);
    this.graph = graph;
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
      tree = new JTree(interactionList);
      tree.setUI(new GsTreeUI());
      tree.setShowsRootHandles(true);
      GsBooleanFunctionTreeRenderer cr = new GsBooleanFunctionTreeRenderer(getPreferredSize().width);
      tree.setCellRenderer(cr);
      tree.setCellEditor(new GsBooleanFunctionTreeEditor(tree, cr));
      tree.setEditable(true);
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
}
