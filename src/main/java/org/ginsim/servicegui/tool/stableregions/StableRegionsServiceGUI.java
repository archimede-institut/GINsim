package org.ginsim.servicegui.tool.stableregions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.service.EStatus;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.core.service.ServiceStatus;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.GUIFor;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.shell.actions.ToolAction;
import org.ginsim.service.tool.stableregions.StableRegionsService;
import org.mangosdk.spi.ProviderFor;

@ProviderFor(ServiceGUI.class)
@GUIFor(StableRegionsService.class)
@ServiceStatus(EStatus.DEVELOPMENT)
public class StableRegionsServiceGUI extends AbstractServiceGUI {

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof RegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new StableRegionsAction((RegulatoryGraph) graph, this));
			return actions;
		}
		return null;
	}

	@Override
	public int getInitialWeight() {
		return W_TOOLS_MAIN + 50;
	}
}

class StableRegionsAction extends ToolAction {

	private final RegulatoryGraph graph;

	public StableRegionsAction(RegulatoryGraph graph, ServiceGUI serviceGUI) {
		super("STR_stableregions", "STR_stableregions_descr", serviceGUI);
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		StableRegionsService srService = ServiceManager.getManager().getService(
				StableRegionsService.class);
		srService.getSCCs(graph);
	}

}