package fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

import fr.univmrs.tagc.GINsim.gui.*;
import fr.univmrs.tagc.GINsim.regulatoryGraph.*;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.graphictree.*;
import fr.univmrs.tagc.GINsim.regulatoryGraph.logicalfunction.neweditor.*;
import fr.univmrs.tagc.GINsim.regulatoryGraph.models.*;
import fr.univmrs.tagc.common.datastore.*;
import fr.univmrs.tagc.common.datastore.gui.*;

public class GsLogicalFunctionPanel extends GsParameterPanel implements ObjectPropertyEditorUI, MouseListener, KeyListener {
  private static final long serialVersionUID = -87854595177707062L;
  private GsIncomingEdgeListModel edgeList = null;
  private GsRegulatoryVertex currentVertex = null;
  private GsLogicalFunctionTreePanel treePanel = null;
  private GsFunctionEditor functionEditor = null;
  private GsRegulatoryGraph graph;
  private GenericPropertyInfo	pinfo;
	private JPanel eastPanel;

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
    functionEditor = new GsFunctionEditor();
    add(getTreePanel(), BorderLayout.CENTER);
		eastPanel = new JPanel(new CardLayout());
		eastPanel.add("edit", functionEditor.getEditPanel());
		eastPanel.add("display", functionEditor.getDisplayPanel());
		add(eastPanel, BorderLayout.EAST);
    functionEditor.getEditPanel().setVisible(false);
		functionEditor.getDisplayPanel().setVisible(false);
    edgeList = new GsIncomingEdgeListModel();
  }
  public void setEditEditorVisible(boolean b) {
		eastPanel.setVisible(b);
		if (b)
			((CardLayout)eastPanel.getLayout()).show(eastPanel, "edit");
  }
	public void setDisplayEditorVisible(boolean b) {
		eastPanel.setVisible(b);
		if (b)
			((CardLayout)eastPanel.getLayout()).show(eastPanel, "display");
	}
  public GsFunctionEditor getFunctionEditor() {
  	return functionEditor;
  }
  public void initEditor(GsTreeInteractionsModel m, GsFunctionPanel fp) {
  	functionEditor.init(m, fp);
  }
  public void setEditedObject(Object obj) {
    if (currentVertex != null) treePanel.setEditedObject(obj);
    if (obj != null && obj instanceof GsRegulatoryVertex) {
      currentVertex = (GsRegulatoryVertex)obj;
      edgeList.setEdge(mainFrame.getGraph().getIncomingEdges(currentVertex));
      treePanel.setEditedObject(obj);
    }
		setEditEditorVisible(false);
  }

  protected JPanel getTreePanel() {
    if (treePanel == null) treePanel = new GsLogicalFunctionTreePanel(graph, this);
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

	public void mouseClicked(MouseEvent e) {
		if (functionEditor.getEditPanel().isShowing()) functionEditor.validate();
		if (functionEditor.getDisplayPanel().isShowing()) {
			functionEditor.getDisplayPanel().cancel();
		}
		setEditEditorVisible(false);

		treePanel.getTree().setEditable(true);
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}
	public void keyTyped(KeyEvent e) {
		/*if ('\t' == e.getKeyChar()) {
			GsTreeExpression exp = treePanel.getSelectedFunction();
			if ((exp != null) && !((Boolean) exp.getProperty("invalid")).booleanValue()) {
				setDisplayEditorVisible(true);
				functionEditor.init(exp, currentVertex, graph, treePanel.getTree());
			}
		}*/
	}
	public void keyPressed(KeyEvent e) {
	}
	public void keyReleased(KeyEvent e) {
	}
}
