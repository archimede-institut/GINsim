package org.ginsim.gui.service.action.interactionanalysis;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.Graph;
import org.ginsim.gui.service.GUIFor;
import org.ginsim.gui.service.GsActionAction;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.service.action.interactionanalysis.InteractionAnalysisService;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;

@ProviderFor(GsServiceGUI.class)
@GUIFor(InteractionAnalysisService.class)
public class InteractionAnalysisServiceGUI implements GsServiceGUI {

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof GsRegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new InteractionAnalysisAction((GsRegulatoryGraph)graph));
			return actions;
		}
		return null;
	}
}

class InteractionAnalysisAction extends GsActionAction {
	
	private final GsRegulatoryGraph graph;
	
	public InteractionAnalysisAction(GsRegulatoryGraph graph) {
		
		super("STR_interactionAnalysis");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO : REFACTORING ACTION
		// TODO: get the parent frame
		new InteractionAnalysisFrame(null, graph);
	}
}