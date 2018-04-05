package org.ginsim.servicegui.tool.pathfinding;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.Graph;
import org.ginsim.core.service.EStatus;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.shell.actions.GenericGraphAction;
import org.ginsim.core.service.ServiceStatus;
import org.ginsim.gui.service.StandaloneGUI;
import org.mangosdk.spi.ProviderFor;


@ProviderFor(ServiceGUI.class)
@StandaloneGUI
@ServiceStatus( EStatus.RELEASED)
public class PathFindingServiceGUI extends AbstractServiceGUI {

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		List<Action> actions = new ArrayList <>();
		actions.add(new PathSearchAction(graph, this));
		return actions;
	}

	@Override
	public int getInitialWeight() {
		return W_GRAPH_SELECTION + 20;
	}

	class PathSearchAction extends GenericGraphAction {

		private PathSearchAction(Graph<?, ?> graph, ServiceGUI serviceGUI) {
			super(graph, "STR_pathFinding", null, "STR_pathFinding_descr", null, serviceGUI);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			new PathFindingFrame(graph);
		}
	}
}

