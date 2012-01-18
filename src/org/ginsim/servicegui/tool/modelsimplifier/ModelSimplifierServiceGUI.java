package org.ginsim.servicegui.tool.modelsimplifier;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.notification.NotificationManager;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.ToolAction;
import org.ginsim.service.tool.modelsimplifier.ModelSimplifierService;
import org.mangosdk.spi.ProviderFor;

/**
 * main method for the model simplification plugin
 */
@ProviderFor(ServiceGUI.class)
@GUIFor(ModelSimplifierService.class)
public class ModelSimplifierServiceGUI implements ServiceGUI {

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof RegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new ModelSimplifierAction((RegulatoryGraph)graph));
			return actions;
		}
		return null;
	}

	@Override
	public int getWeight() {
		return W_MANIPULATION;
	}
}

class ModelSimplifierAction extends ToolAction {

	private final RegulatoryGraph graph;
	public ModelSimplifierAction(RegulatoryGraph graph) {
		super("STR_reduce", "STR_reduce_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (graph.getNodeCount() < 1) {
            NotificationManager.publishWarning( graph, graph instanceof RegulatoryGraph ? "STR_emptyGraph" : "STR_notRegGraph");
            return;
		}

		// TODO: reset edit mode
		// mframe.getActions().setCurrentMode(GsActions.MODE_DEFAULT, 0, false);
		new ModelSimplifierConfigDialog(graph);
	}
}
