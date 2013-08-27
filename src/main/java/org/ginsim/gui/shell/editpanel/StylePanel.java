package org.ginsim.gui.shell.editpanel;

import java.awt.Component;
import java.awt.Frame;
import java.util.List;

import javax.swing.JPanel;

import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.view.EdgeAttributesReader;
import org.ginsim.core.graph.view.EdgeStyle;
import org.ginsim.core.graph.view.NodeAttributesReader;
import org.ginsim.core.graph.view.NodeStyle;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.graph.GraphSelection;

public class StylePanel extends JPanel implements EditTab {

	private final NodeStyle defaultNodeStyle;
	private final EdgeStyle defaultEdgeStyle;
	private final NodeAttributesReader vReader;
	private final EdgeAttributesReader eReader;
	
	protected final GraphGUI<?, ?, ?> gui;
    protected final Graph<?,?> graph;
    protected final Frame frame;
	
	public StylePanel(GraphGUI<?, ?, ?> gui) {

    	this.gui = gui;
    	this.graph = gui.getGraph();
    	this.frame = GUIManager.getInstance().getFrame(graph);
		
		Graph<?,?> graph = gui.getGraph();
		vReader = graph.getNodeAttributeReader();
		defaultNodeStyle = vReader.getDefaultNodeStyle();
		eReader = graph.getEdgeAttributeReader();
		defaultEdgeStyle = eReader.getDefaultEdgeStyle();

		initialize();
		reload();
	}

	
	private void reload() {
		// TODO Auto-generated method stub
		
	}


	private void initialize() {
		// TODO Auto-generated method stub
		
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
		switch (selection.getSelectionType()) {
//		case SEL_NODE:
//			setEditedItem(selection.getSelectedNodes().get(0));
//			return true;
//		case SEL_EDGE:
//			setEditedItem(selection.getSelectedEdges().get(0));
//			return true;
//		case SEL_MULTIPLE:
//			if (selection.getSelectedNodes() != null) {
//				if (selection.getSelectedNodes().size() == 1) {
//					setEditedItem(selection.getSelectedNodes().get(0));
//				} else {
//					setEditedItem(selection.getSelectedNodes());
//				}
//			} else if (selection.getSelectedEdges().size() == 1) {
//				setEditedItem(selection.getSelectedEdges().get(0));
//			} else {
//				setEditedItem(selection.getSelectedEdges());
//			}
//			return true;
		default:
			return false;
		}
	}

}
