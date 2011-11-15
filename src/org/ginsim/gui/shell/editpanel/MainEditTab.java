package org.ginsim.gui.shell.editpanel;

import java.awt.CardLayout;
import java.awt.Component;

import javax.swing.JPanel;

import org.ginsim.graph.common.Graph;
import org.ginsim.gui.graph.GUIEditor;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.graph.GraphSelection;

public class MainEditTab extends JPanel implements EditTab {

	private static final String GRAPH_NAME="Graph", NODE_NAME="Node", EDGE_NAME="Edge";
	
	private final String title;
	private final GUIEditor mainPanel, nodePanel, edgePanel;
    private final CardLayout cards;
    
	public MainEditTab( GraphGUI gui) {
		cards = new CardLayout();
		setLayout(cards);
		
		Graph graph = gui.getGraph();
		this.title = gui.getEditingTabLabel();
		this.mainPanel = gui.getMainEditionPanel();
		this.nodePanel = gui.getNodeEditionPanel();
		this.edgePanel = gui.getEdgeEditionPanel();
		
		add(mainPanel.getComponent(), GRAPH_NAME);
		add(nodePanel.getComponent(), NODE_NAME);
		add(edgePanel.getComponent(), EDGE_NAME);
	}
	
	@Override
	public Component getComponent() {
		return this;
	}

	@Override
	public String getTitle() {
		return title;
	}

	@Override
	public boolean isActive(GraphSelection<?, ?> selection) {
		
		String activePanel = null;
		switch (selection.getSelectionType()) {
		case SEL_NONE:
			activePanel = GRAPH_NAME;
			break;
		case SEL_NODE:
			activePanel = NODE_NAME;
			nodePanel.setEditedItem(selection.getSelectedNodes().get(0));
			break;
		case SEL_EDGE:
			activePanel = EDGE_NAME;
			edgePanel.setEditedItem(selection.getSelectedEdges().get(0));
			break;
		default:
			return false;
		}
		
		cards.show(this, activePanel);
		// TODO: provide the selection to the active tab
		
		return true;
	}
}
