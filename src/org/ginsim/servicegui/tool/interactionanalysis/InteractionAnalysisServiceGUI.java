package org.ginsim.servicegui.tool.interactionanalysis;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.GUIManager;
import org.ginsim.service.tool.interactionanalysis.InteractionAnalysisService;
import org.ginsim.servicegui.ServiceGUI;
import org.ginsim.servicegui.common.GUIFor;
import org.ginsim.servicegui.common.ToolAction;
import org.mangosdk.spi.ProviderFor;


@ProviderFor(ServiceGUI.class)
@GUIFor(InteractionAnalysisService.class)
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