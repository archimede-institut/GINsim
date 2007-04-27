package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction;

import javax.swing.*;
import java.awt.BorderLayout;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.GsTreeInteractionsModel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.GsTreeInteractionsCellRenderer;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.GsTreeInteractionsCellEditor;
import fr.univmrs.ibdm.GINsim.gui.GsParameterPanel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction.graphictree.GsTreeElement;
import fr.univmrs.ibdm.GINsim.graph.GsGraphNotificationMessage;

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
      interactionList = new GsTreeInteractionsModel(graph);
      tree = new JTree(interactionList);
      tree.setShowsRootHandles(true);
      GsTreeInteractionsCellRenderer cr = new GsTreeInteractionsCellRenderer(getPreferredSize().width);
      tree.setCellRenderer(cr);
      tree.setCellEditor(new GsTreeInteractionsCellEditor(tree, cr));
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
      interactionList.addExpression(val, currentVertex, tbp);
      ((GsTreeInteractionsModel)tree.getModel()).fireTreeStructureChanged((GsTreeElement)tree.getModel().getRoot());
      tree.expandPath(interactionList.getPath(val, tbp.getRoot().toString()));
      graph.getVertexAttributePanel().setEditedObject(currentVertex);
    }
  }
}
