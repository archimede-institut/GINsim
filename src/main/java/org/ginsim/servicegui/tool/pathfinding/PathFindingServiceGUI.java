package org.ginsim.servicegui.tool.pathfinding;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.view.css.Selector;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GenericGraphAction;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.gui.service.common.StandaloneGUI;
import org.mangosdk.spi.ProviderFor;


@ProviderFor(ServiceGUI.class)
@StandaloneGUI
@ServiceStatus( ServiceStatus.RELEASED)
public class PathFindingServiceGUI extends AbstractServiceGUI {

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		List<Action> actions = new ArrayList<Action>();
		actions.add(new PathSearchAction(graph, this));
		return actions;
	}

	@Override
	public int getInitialWeight() {
		return W_GRAPH_SELECTION + 20;
	}
}

class PathSearchAction extends GenericGraphAction {

	public PathSearchAction(Graph<?, ?> graph, ServiceGUI serviceGUI) {
		super(graph, "STR_pathFinding", null, "STR_pathFinding_descr", null, serviceGUI);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
        new PathFindingFrame(graph);
	}
}
