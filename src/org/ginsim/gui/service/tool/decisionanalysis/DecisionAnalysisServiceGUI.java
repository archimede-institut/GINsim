package org.ginsim.gui.service.tool.decisionanalysis;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.hierachicaltransitiongraph.HierarchicalTransitionGraph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.GsToolsAction;
import org.ginsim.service.tool.decisionanalysis.DecisionAnalysisService;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.common.Debugger;


@ProviderFor( ServiceGUI.class)
@GUIFor( DecisionAnalysisService.class)
public class DecisionAnalysisServiceGUI implements ServiceGUI {
	
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof HierarchicalTransitionGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new DecisionAnalysisAction((HierarchicalTransitionGraph)graph));
			return actions;
		}
		return null;
	}
}


class DecisionAnalysisAction extends GsToolsAction {

	private final HierarchicalTransitionGraph graph;
	
	public DecisionAnalysisAction( HierarchicalTransitionGraph graph) {
		
		super( "STR_htg_decision_analysis", "STR_htg_decision_analysis_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
        // TODO : REFACTORING ACTION		
		// TODO : what is ref? Is this test useful? Ref was set to 0 in the GsPluggableActionDescriptor definition in the getT_action
		//if (ref == 0) {
		try{
			new DecisionAnalysisFrame( GUIManager.getInstance().getFrame( graph), graph);
		}
		catch( GsException ge){
    		// TODO : REFACTORING ACTION
    		// TODO : Launch a message box to the user
    		Debugger.log( "Unable to execute the service" + ge);
		}
		//}
	}
	
}
