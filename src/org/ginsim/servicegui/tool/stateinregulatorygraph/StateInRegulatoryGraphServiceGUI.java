package org.ginsim.servicegui.tool.stateinregulatorygraph;

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
import org.ginsim.service.tool.stateinregulatorygraph.StateInRegulatoryGraphService;
import org.mangosdk.spi.ProviderFor;


@ProviderFor( ServiceGUI.class)
@GUIFor( StateInRegulatoryGraphService.class)
@ServiceStatus( ServiceStatus.PUBLISHED)
public class StateInRegulatoryGraphServiceGUI implements ServiceGUI {

	
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		
		if (graph instanceof RegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new StateInRegulatoryGraphAction((RegulatoryGraph)graph));
			return actions;
		}
		return null;
	}


	@Override
	public int getWeight() {
		return W_INFO + 2;
	}
}

class StateInRegulatoryGraphAction extends ToolAction {

	private final RegulatoryGraph graph;
	
	public StateInRegulatoryGraphAction(RegulatoryGraph graph) {
		
		super( "STR_stateInRegGraph", "STR_stateInRegGraph_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

        new StateInRegGraphFrame( GUIManager.getInstance().getFrame( graph), graph);
	}
	
}
