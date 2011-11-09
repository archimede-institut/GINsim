package org.ginsim.gui.service.interactionAnalysis;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.ginsim.graph.Graph;
import org.ginsim.gui.service.GsServiceGUI;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;

@ProviderFor(GsServiceGUI.class)
public class InteractionAnalysisPlugin implements GsServiceGUI {

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

class InteractionAnalysisAction extends AbstractAction {
	private final GsRegulatoryGraph graph;
	
	public InteractionAnalysisAction(GsRegulatoryGraph graph) {
		super("STR_interactionAnalysis");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO: get the parent frame
		new InteractionAnalysisFrame(null, graph);
	}
}