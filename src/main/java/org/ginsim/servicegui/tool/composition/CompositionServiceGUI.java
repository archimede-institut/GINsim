package org.ginsim.servicegui.tool.composition;

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
import org.ginsim.service.tool.composition.CompositionService;

import org.mangosdk.spi.ProviderFor;

/**
 * Register the Composition Service
 * 
 * @author Nuno D. Mendes
 */
@ProviderFor(ServiceGUI.class)
@GUIFor(CompositionService.class)
@ServiceStatus(EStatus.RELEASED)
public class CompositionServiceGUI extends AbstractServiceGUI {

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof RegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new CompositionAction((RegulatoryGraph) graph, this));

			return actions;
		} else {
			// available composition actions for graphs other than LRGs/LRMs
			return null;
		}
	}

	@Override
	public int getInitialWeight() {
		return W_TOOLS_MAIN + 51;
	}
}

class CompositionAction extends ToolAction {
	private static final long serialVersionUID = -1993709762198153932L;
	private final RegulatoryGraph graph;

	public CompositionAction(RegulatoryGraph graph, ServiceGUI serviceGUI) {
		super("STR_compose", "STR_compose_descr", serviceGUI);
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (graph.getNodeCount() < 1) {
			NotificationManager.publishWarning(graph,
					graph instanceof RegulatoryGraph ? "STR_emptyGraph"
							: "STR_notRegGraph");
			return;
		}

		new CompositionConfigDialog(graph).setVisible(true);
	}

}
