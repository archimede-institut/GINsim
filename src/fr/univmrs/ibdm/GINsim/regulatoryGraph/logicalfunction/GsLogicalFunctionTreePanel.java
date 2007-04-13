package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction;

import javax.swing.*;
import java.util.Vector;
import java.awt.BorderLayout;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import java.util.Iterator;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsEdgeIndex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.parser.TBooleanTreeNode;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.GsTreeInteractionsModel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.GsTreeInteractionsCellRenderer;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.GsTreeInteractionsCellEditor;
import fr.univmrs.ibdm.GINsim.gui.GsParameterPanel;
import javax.swing.tree.TreePath;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.GsTreeElement;

public class GsLogicalFunctionTreePanel extends GsParameterPanel {
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
    tree.setModel(interactionList);
    tree.repaint();
  }
  private JTree getJTree(GsRegulatoryGraph graph) {
    if (tree == null) {
      Vector v_ok = new Vector();
      interactionList = new GsTreeInteractionsModel(graph);
      tree = new JTree(interactionList);
      tree.setShowsRootHandles(true);
      tree.setCellRenderer(new GsTreeInteractionsCellRenderer());
      tree.setCellEditor(new GsTreeInteractionsCellEditor(tree, tree.getCellRenderer()));
      tree.setEditable(true);
    }
    return tree;
  }
  public void addFunctionList(GsLogicalFunctionList list, short val, GsRegulatoryVertex currentVertex, TBooleanTreeNode root) {
    Iterator it = list.getData().iterator(), it2;
    Vector v;
    GsEdgeIndex edgeIndex;
    GsLogicalFunctionListElement element;

    interactionList.setNode(currentVertex);
    interactionList.addValue(val);
    interactionList.addExpression(val, root);
    //tree.scrollPathToVisible(new TreePath(interactionList.getExpression(root));
    while (it.hasNext()) {
      it2 = ((Vector)it.next()).iterator();
      v = new Vector();
      while (it2.hasNext()) {
        element = (GsLogicalFunctionListElement)it2.next();
        edgeIndex = new GsEdgeIndex(element.getEdge(), element.getIndex());
        v.addElement(edgeIndex);
      }
      interactionList.setActivesEdges(v, val);
      interactionList.addFunction(val, root.toString(), v);
    }
    interactionList.parseFunctions();
    ((GsTreeInteractionsModel)tree.getModel()).fireTreeStructureChanged((GsTreeElement)tree.getModel().getRoot());
    currentVertex.setInteractionsModel(interactionList);
  }
}
