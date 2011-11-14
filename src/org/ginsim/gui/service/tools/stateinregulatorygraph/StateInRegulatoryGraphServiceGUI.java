package org.ginsim.gui.service.tools.stateinregulatorygraph;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.common.Graph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.GsToolsAction;
import org.ginsim.service.action.stateinregulatorygraph.StateInRegulatoryGraphService;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;

@ProviderFor( GsServiceGUI.class)
@GUIFor( StateInRegulatoryGraphService.class)
public class StateInRegulatoryGraphServiceGUI implements GsServiceGUI {

	
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		
		if (graph instanceof GsRegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new StateInRegulatoryGraphAction((GsRegulatoryGraph)graph));
			return actions;
		}
		return null;
	}

}

class StateInRegulatoryGraphAction extends GsToolsAction {

	private final GsRegulatoryGraph graph;
	
	public StateInRegulatoryGraphAction(GsRegulatoryGraph graph) {
		
		super( "STR_stateInRegGraph", "STR_stateInRegGraph_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

        new GsStateInRegGraphFrame( GUIManager.getInstance().getFrame( graph), graph);
	}
	
}
