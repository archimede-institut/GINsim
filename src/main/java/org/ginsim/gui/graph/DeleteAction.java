package org.ginsim.gui.graph;

import org.ginsim.common.application.LogManager;
import org.ginsim.common.application.Txt;
import org.ginsim.core.graph.Edge;
import org.ginsim.core.graph.Graph;
import org.ginsim.gui.GUIManager;
import org.ginsim.commongui.dialog.GUIMessageUtils;

public class DeleteAction extends EditAction {

	Graph graph;
	GraphGUI gui = null;
	
	public DeleteAction(Graph<?,?> graph) {
		super( EditMode.DELETE, Txt.t("STR_deleteSelection_descr"), "edit-delete.png");
		this.graph = graph;
		this.gui = GUIManager.getInstance().getGraphGUI(graph);
	}
	
	public void performed(EditActionManager manager) {
		boolean yes_answer = GUIMessageUtils.openConfirmationDialog("Are you sure, that can be lead to incorrect graph", "Delete component?");
		if (!yes_answer){
			return;
		}
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
		
		// extend selection for deletion (helps to mark them as damaged)
		selection.extendSelectionToIncomingEdges();
		selection.extendSelectionToOutgoingEdges();
		
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
		
		selection.unselectAll();
        gui.repaint();
	}

}
