package org.ginsim.gui.service.tool.pathfinding;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.common.Graph;
import org.ginsim.graph.view.css.Selector;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.ToolAction;
import org.ginsim.gui.service.common.StandaloneGUI;
import org.mangosdk.spi.ProviderFor;


@ProviderFor(ServiceGUI.class)
@StandaloneGUI
public class PathFindingServiceGUI implements ServiceGUI {

	static {
		Selector.registerSelector(PathFindingSelector.IDENTIFIER, PathFindingSelector.class);
	}

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		List<Action> actions = new ArrayList<Action>();
		actions.add(new PathSearchAction(graph));
		return actions;
	}
}

class PathSearchAction extends ToolAction {

	private final Graph<?, ?> graph;
	
	public PathSearchAction(Graph<?, ?> graph) {
		super("STR_pathFinding", "STR_pathFinding_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
        new PathFindingFrame(graph);
	}
}