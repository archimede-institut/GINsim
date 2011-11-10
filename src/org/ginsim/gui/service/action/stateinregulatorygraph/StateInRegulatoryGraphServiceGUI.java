package org.ginsim.gui.service.action.stateinregulatorygraph;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.Graph;
import org.ginsim.gui.service.GUIFor;
import org.ginsim.gui.service.GsActionAction;
import org.ginsim.gui.service.GsServiceGUI;
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

class StateInRegulatoryGraphAction extends GsActionAction {

	private final GsRegulatoryGraph graph;
	
	public StateInRegulatoryGraphAction(GsRegulatoryGraph graph) {
		
		super( "STR_stateInRegGraph", "STR_stateInRegGraph_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

        // TODO : REFACTORING ACTION
        // TODO: get the parent frame
        new GsStateInRegGraphFrame( null, graph);
	}
	
}
