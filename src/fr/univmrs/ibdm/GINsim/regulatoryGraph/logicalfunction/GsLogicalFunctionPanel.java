package fr.univmrs.ibdm.GINsim.regulatoryGraph.logicalfunction;

import javax.swing.JPanel;

import fr.univmrs.ibdm.GINsim.gui.GsParameterPanel;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.ibdm.GINsim.regulatoryGraph.models.GsIncomingEdgeListModel;
import java.awt.BorderLayout;

public class GsLogicalFunctionPanel extends GsParameterPanel {
  private static final long serialVersionUID = -87854595177707062L;
  private GsIncomingEdgeListModel edgeList = null;
  private GsRegulatoryVertex currentVertex = null;
  private GsLogicalFunctionTreePanel treePanel = null;
  private GsRegulatoryGraph graph;

  public GsLogicalFunctionPanel(GsRegulatoryGraph graph) {
    super();
    setMainFrame(graph.getGraphManager().getMainFrame());
    this.graph = graph;
    initialize();
  }

  /**
   * This method initializes this
   */
  private void initialize() {
    setLayout(new BorderLayout());
    add(getTreePanel(), BorderLayout.CENTER);
    edgeList = new GsIncomingEdgeListModel();
  }
  public void setEditedObject(Object obj) {
    if (currentVertex != null) {
      treePanel.setEditedObject(obj);
    }
    if (obj != null && obj instanceof GsRegulatoryVertex) {
      currentVertex = (GsRegulatoryVertex)obj;
      edgeList.setEdge(mainFrame.getGraph().getGraphManager().getIncomingEdges(currentVertex));
    }
  }

  protected JPanel getTreePanel() {
    if (treePanel == null)
      treePanel = new GsLogicalFunctionTreePanel(graph);
    return treePanel;
  }
}
