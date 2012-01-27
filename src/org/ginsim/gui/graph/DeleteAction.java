package org.ginsim.gui.graph;

import org.ginsim.common.utils.log.LogManager;
import org.ginsim.core.graph.common.Edge;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.gui.GUIManager;

public class DeleteAction extends EditAction {

	Graph graph;
	GraphGUI gui = null;
	
	public DeleteAction(Graph<?,?> graph) {
		super(EditMode.DELETE, "Delete selection", "edit-delete.png");
		this.graph = graph;
		this.gui = GUIManager.getInstance().getGraphGUI(graph);
	}
	
	public void performed(EditActionManager manager) {
		if (gui == null) {
			gui = GUIManager.getInstance().getGraphGUI(graph);
			if (gui == null) {
				LogManager.error("No GUI for the graph");
				return;
			}
		}
		GraphSelection<?, ?> selection = gui.getSelection();
		if (selection == null) {
			LogManager.debug("empty selection");
			return;
		}
		
		if (selection.getSelectedEdges() != null) {
			for (Edge<?> e: selection.getSelectedEdges()) {
				graph.removeEdge(e);
			}
		}

		if (selection.getSelectedNodes() != null) {
			for (Object n: selection.getSelectedNodes()) {
				graph.removeNode(n);
			}
		}
	}

}
