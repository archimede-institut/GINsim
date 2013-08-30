package org.ginsim.gui.shell.editpanel;

import java.awt.Component;
import java.awt.Frame;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.event.ListDataListener;

import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.graph.view.style.EdgeStyle;
import org.ginsim.core.graph.view.style.NodeStyle;
import org.ginsim.core.graph.view.style.Style;
import org.ginsim.core.graph.view.style.StyleManager;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.graph.GraphSelection;

public class StyleTab extends JPanel implements EditTab {

    protected final Graph<?,?> graph;
    
    private final StyleComboModel styleModel;
    
    private StyleEditionPanel stylePanel;
    private JComboBox styleSelection;
	
	public StyleTab(GraphGUI<?, ?, ?> gui) {

    	this.graph = gui.getGraph();
    	
    	StyleManager manager = graph.getStyleManager();
    	this.stylePanel = new StyleEditionPanel(null, manager);
    	this.styleModel = new StyleComboModel(manager, stylePanel);
    	this.styleSelection = new JComboBox(styleModel);

		add(styleSelection);
		add(stylePanel);
	}

	@Override
	public final Component getComponent() {
		return this;
	}


	@Override
	public String getTitle() {
		return "Style";
	}

	@Override
	public boolean isActive(GraphSelection<?, ?> selection) {

		// TODO: handle multiple edition
		switch (selection.getSelectionType()) {
		case SEL_NODE:
			styleModel.editNode(selection.getSelectedNodes().get(0));
			return true;
		case SEL_MULTIPLE:
			if (selection.getSelectedNodes() != null) {
				styleModel.editNode(selection.getSelectedNodes().get(0));
				return true;
			}
		case SEL_EDGE:
			styleModel.editEdge(selection.getSelectedEdges().get(0));
			return true;
		default:
			styleModel.disableEdit();
			return false;
		}
	}
}

class StyleComboModel extends AbstractListModel implements ComboBoxModel {

	private final StyleManager styleManager;
	
	private final List<NodeStyle> nodeStyles;
    private final List<EdgeStyle> edgeStyles;
    private final StyleEditionPanel editPanel;

    private Object selectedNode = null;
    private Edge selectedEdge = null;
    
	public StyleComboModel(StyleManager styleManager, StyleEditionPanel editPanel) {
		this.styleManager = styleManager;
		this.nodeStyles = styleManager.getNodeStyles();
		this.edgeStyles = styleManager.getEdgeStyles();
		this.editPanel = editPanel;
	}
	
	public void disableEdit() {
		this.selectedEdge = null;
		this.selectedNode = null;
		this.styles = null;
		setSelected(null);
	}

	private List styles = null;
	private Style selected = null;
	
	public void editNode(Object node) {
		this.selectedEdge = null;
		this.selectedNode = node;
		this.styles = nodeStyles;
		setSelected(styleManager.getUsedNodeStyle(node));
	}

	public void editEdge(Edge edge) {
		this.selectedEdge = edge;
		this.selectedNode = null;
		this.styles = edgeStyles;
		setSelected(styleManager.getUsedEdgeStyle(edge));
	}

	@Override
	public Object getElementAt(int index) {
		if (styles == null || index >= styles.size()) {
			return null;
		}
		return styles.get(index);
	}

	@Override
	public int getSize() {
		if (styles == null) {
			return 0;
		}
		return styles.size();
	}

	@Override
	public Object getSelectedItem() {
		return selected;
	}

	@Override
	public void setSelectedItem(Object item) {
		if (selectedNode != null && item instanceof NodeStyle) {
			styleManager.applyNodeStyle(selectedNode, (NodeStyle)item);
			setSelected(styleManager.getUsedNodeStyle(selectedNode));
		} else if (selectedEdge != null && item instanceof EdgeStyle) {
			styleManager.applyEdgeStyle(selectedEdge, (EdgeStyle)item);
			setSelected(styleManager.getUsedEdgeStyle(selectedEdge));
		}
	}
	
	private void setSelected(Style sel) {
		this.selected = sel;
		editPanel.setStyle(sel);
		fireContentsChanged(this, 0, getSize());
	}
}