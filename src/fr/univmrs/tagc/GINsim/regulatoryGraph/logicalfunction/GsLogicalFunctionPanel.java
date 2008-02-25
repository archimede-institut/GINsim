package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import fr.univmrs.tagc.GINsim.gui.GsParameterPanel;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryVertex;
import fr.univmrs.tagc.GINsim.regulatoryGraph.models.GsIncomingEdgeListModel;
import fr.univmrs.tagc.common.datastore.GenericPropertyInfo;
import fr.univmrs.tagc.common.datastore.ObjectPropertyEditorUI;
import fr.univmrs.tagc.common.datastore.gui.GenericPropertyHolder;

public class GsLogicalFunctionPanel extends GsParameterPanel
	implements ObjectPropertyEditorUI {
  private static final long serialVersionUID = -87854595177707062L;
  private GsIncomingEdgeListModel edgeList = null;
  private GsRegulatoryVertex currentVertex = null;
  private GsLogicalFunctionTreePanel treePanel = null;
  private GsRegulatoryGraph graph;
  private GenericPropertyInfo	pinfo;

  public GsLogicalFunctionPanel() {
  }

  public GsLogicalFunctionPanel(GsRegulatoryGraph graph) {
    super();
    this.graph = graph;
    initialize();
  }

  /**
   * This method initializes this
   */
  private void initialize() {
    setMainFrame(graph.getGraphManager().getMainFrame());
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
    if (treePanel == null) {
		treePanel = new GsLogicalFunctionTreePanel(graph);
	}
    return treePanel;
  }

	public void apply() {
	}
	
	public void refresh(boolean force) {
		setEditedObject(pinfo.getRawValue());
	}
	
	public void setEditedProperty(GenericPropertyInfo pinfo, GenericPropertyHolder panel) {
		this.pinfo = pinfo;
	    this.graph = (GsRegulatoryGraph)pinfo.data;
	    initialize();
		panel.addField(this, pinfo, 0);
	}
}
