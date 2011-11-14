package org.ginsim.gui.service.tools.pathfinding;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.Graph;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.gui.service.common.GsToolsAction;
import org.ginsim.gui.service.common.StandaloneGUI;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.css.Selector;

@ProviderFor(GsServiceGUI.class)
@StandaloneGUI
public class PathFindingServiceGUI implements GsServiceGUI {

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

class PathSearchAction extends GsToolsAction {

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
