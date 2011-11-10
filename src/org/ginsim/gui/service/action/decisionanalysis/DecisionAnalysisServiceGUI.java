package org.ginsim.gui.service.action.decisionanalysis;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.Graph;
import org.ginsim.gui.service.GUIFor;
import org.ginsim.gui.service.GsActionAction;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.service.action.decisionanalysis.DecisionAnalysisService;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.hierachicalTransitionGraph.GsHierarchicalTransitionGraph;

@ProviderFor( GsServiceGUI.class)
@GUIFor( DecisionAnalysisService.class)
public class DecisionAnalysisServiceGUI implements GsServiceGUI {
	
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof GsHierarchicalTransitionGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new DecisionAnalysisAction((GsHierarchicalTransitionGraph)graph));
			return actions;
		}
		return null;
	}
}


class DecisionAnalysisAction extends GsActionAction {

	private final GsHierarchicalTransitionGraph graph;
	
	public DecisionAnalysisAction( GsHierarchicalTransitionGraph graph) {
		
		super( "STR_htg_decision_analysis", "STR_htg_decision_analysis_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
        // TODO : REFACTORING ACTION
        // TODO: get the parent frame		
		// TODO : what is ref? Is this test useful? Ref was set to 0 in the GsPluggableActionDescriptor definition in the getT_action
		//if (ref == 0) {
			new GsDecisionAnalysisFrame( null, graph);
		//}
	}
	
}
