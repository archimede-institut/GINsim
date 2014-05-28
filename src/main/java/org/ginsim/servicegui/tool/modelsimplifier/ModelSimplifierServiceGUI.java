package org.ginsim.servicegui.tool.modelsimplifier;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.notification.NotificationManager;
import org.ginsim.core.service.EStatus;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.GUIFor;
import org.ginsim.core.service.ServiceStatus;
import org.ginsim.gui.shell.actions.ToolAction;
import org.ginsim.service.tool.modelreduction.ModelSimplifierService;
import org.mangosdk.spi.ProviderFor;

/**
 * main method for the model simplification plugin
 */
@ProviderFor(ServiceGUI.class)
@GUIFor(ModelSimplifierService.class)
@ServiceStatus( EStatus.RELEASED)
public class ModelSimplifierServiceGUI extends AbstractServiceGUI {

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof RegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new ModelSimplifierAction((RegulatoryGraph)graph, this));
			return actions;
		}
		return null;
	}

	@Override
	public int getInitialWeight() {
		return W_TOOLS_MAIN + 50;
	}
}

class ModelSimplifierAction extends ToolAction {

	private static final long serialVersionUID = 2341667285128867588L;
	private final RegulatoryGraph graph;
	public ModelSimplifierAction(RegulatoryGraph graph, ServiceGUI serviceGUI) {
		super("STR_reduce", "STR_reduce_descr", serviceGUI);
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
