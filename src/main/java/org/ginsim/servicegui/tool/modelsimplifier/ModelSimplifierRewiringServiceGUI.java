package org.ginsim.servicegui.tool.modelsimplifier;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.notification.NotificationManager;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.gui.service.common.ToolAction;
import org.ginsim.gui.utils.dialog.stackdialog.HandledStackDialog;
import org.ginsim.service.tool.modelsimplifier.ModelSimplifierService;
import org.mangosdk.spi.ProviderFor;

/**
 * main method for the model simplification plugin
 */
@ProviderFor(ServiceGUI.class)
@GUIFor(ModelSimplifierService.class)
@ServiceStatus( ServiceStatus.UNDER_DEVELOPMENT)
public class ModelSimplifierRewiringServiceGUI extends AbstractServiceGUI {

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof RegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new ModelRewiringAction((RegulatoryGraph)graph, this));
			return actions;
		}
		return null;
	}

	@Override
	public int getInitialWeight() {
		return W_TOOLS_MAIN + 51;
	}
}

class ModelRewiringAction extends ToolAction {
	private static final long serialVersionUID = 2437799457065501757L;
	private final RegulatoryGraph graph;
	public ModelRewiringAction(RegulatoryGraph graph, ServiceGUI serviceGUI) {
		super("STR_rewire", "STR_rewire_descr", serviceGUI);
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (graph.getNodeCount() < 1) {
            NotificationManager.publishWarning( graph, graph instanceof RegulatoryGraph ? "STR_emptyGraph" : "STR_notRegGraph");
            return;
		}

		// TODO: reset edit mode
		new HandledStackDialog(new RewiringGUI( graph));
	}
}
