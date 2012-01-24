package org.ginsim.servicegui.tool.interactionanalysis;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.gui.service.common.ToolAction;
import org.ginsim.service.tool.interactionanalysis.InteractionAnalysisService;
import org.mangosdk.spi.ProviderFor;


@ProviderFor(ServiceGUI.class)
@GUIFor(InteractionAnalysisService.class)
@ServiceStatus( ServiceStatus.UNDER_DEVELOPMENT)
public class InteractionAnalysisServiceGUI implements ServiceGUI {

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof RegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new InteractionAnalysisAction<RegulatoryGraph>((RegulatoryGraph)graph));
			return actions;
		}
		return null;
	}

	@Override
	public int getWeight() {
		return W_INFO + 1;
	}
}

class InteractionAnalysisAction<G extends Graph<?, ?>> extends ToolAction {
	private static final long serialVersionUID = 216892824635448627L;
	private final RegulatoryGraph graph;
	
	public InteractionAnalysisAction(RegulatoryGraph graph) {
		super("STR_interactionAnalysis");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		new InteractionAnalysisFrame( GUIManager.getInstance().getFrame( graph), graph);
	}
}