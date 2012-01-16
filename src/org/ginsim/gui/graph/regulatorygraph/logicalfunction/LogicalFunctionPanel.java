package org.ginsim.gui.graph.regulatorygraph.logicalfunction;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryNode;
import org.ginsim.core.graph.regulatorygraph.logicalfunction.graphictree.TreeInteractionsModel;
import org.ginsim.gui.graph.regulatorygraph.logicalfunction.graphictree.FunctionPanelImpl;
import org.ginsim.gui.graph.regulatorygraph.logicalfunction.neweditor.FunctionEditor;
import org.ginsim.gui.graph.regulatorygraph.models.IncomingEdgeListModel;
import org.ginsim.gui.shell.editpanel.AbstractParameterPanel;
import org.ginsim.gui.utils.data.GenericPropertyHolder;
import org.ginsim.gui.utils.data.GenericPropertyInfo;
import org.ginsim.gui.utils.data.ObjectPropertyEditorUI;


public class LogicalFunctionPanel extends AbstractParameterPanel implements ObjectPropertyEditorUI, MouseListener, KeyListener {
	private static final long serialVersionUID = -87854595177707062L;
	private IncomingEdgeListModel edgeList = null;
	private RegulatoryNode currentNode = null;
	private LogicalFunctionTreePanel treePanel = null;
	private FunctionEditor functionEditor = null;
	private RegulatoryGraph graph;
	private GenericPropertyInfo	pinfo;
	private JPanel eastPanel;

	public LogicalFunctionPanel(RegulatoryGraph graph) {
		super(graph);
		this.graph = graph;
		initialize();
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		setLayout(new BorderLayout());
		functionEditor = new FunctionEditor();
		add(getTreePanel(), BorderLayout.CENTER);
		eastPanel = new JPanel(new CardLayout());
		eastPanel.add("edit", functionEditor.getEditPanel());
		eastPanel.add("display", functionEditor.getDisplayPanel());
		add(eastPanel, BorderLayout.EAST);
		functionEditor.getEditPanel().setVisible(false);
		functionEditor.getDisplayPanel().setVisible(false);
		edgeList = new IncomingEdgeListModel();
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
	public FunctionEditor getFunctionEditor() {
		return functionEditor;
	}
	public void initEditor(TreeInteractionsModel m, FunctionPanelImpl fp) {
		functionEditor.init(m, fp);
	}
	public void setEditedItem(Object obj) {
		if (currentNode != null) treePanel.setEditedItem(obj);
		if (obj != null && obj instanceof RegulatoryNode) {
			currentNode = (RegulatoryNode)obj;
			edgeList.setEdge(graph.getIncomingEdges(currentNode));
			treePanel.setEditedItem(obj);
		}
		setEditEditorVisible(false);
	}

	protected JPanel getTreePanel() {
		if (treePanel == null) {
			treePanel = new LogicalFunctionTreePanel(graph, this);
		}
		return treePanel;
	}
	public void apply() {
	}

	public void refresh(boolean force) {
		setEditedItem(pinfo.getRawValue());
	}

	public void setEditedProperty(GenericPropertyInfo pinfo, GenericPropertyHolder panel) {
		this.pinfo = pinfo;
		this.graph = (RegulatoryGraph)pinfo.data;
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
			TreeExpression exp = treePanel.getSelectedFunction();
			if ((exp != null) && !((Boolean) exp.getProperty("invalid")).booleanValue()) {
				setDisplayEditorVisible(true);
				functionEditor.init(exp, currentNode, graph, treePanel.getTree());
			}
		}*/
	}
	public void keyPressed(KeyEvent e) {
	}
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void release() {
	}
}
