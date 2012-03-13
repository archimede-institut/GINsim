package org.ginsim.servicegui.tool.stateinregulatorygraph;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.GenericGraphAction;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.service.tool.stateinregulatorygraph.StateInRegulatoryGraphService;
import org.mangosdk.spi.ProviderFor;


@ProviderFor( ServiceGUI.class)
@GUIFor( StateInRegulatoryGraphService.class)
@ServiceStatus( ServiceStatus.RELEASED)
public class StateInRegulatoryGraphServiceGUI extends AbstractServiceGUI {

	
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		
		if (graph instanceof RegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new StateInRegulatoryGraphAction((RegulatoryGraph)graph, this));
			return actions;
		}
		return null;
	}


	@Override
	public int getInitialWeight() {
		return W_GRAPH_COLORIZE + 10;
	}
}

class StateInRegulatoryGraphAction extends GenericGraphAction {

	
	public StateInRegulatoryGraphAction(RegulatoryGraph graph, ServiceGUI serviceGUI) {
		
		super( graph, "STR_stateInRegGraph", null, "STR_stateInRegGraph_descr", null, serviceGUI);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

        new StateInRegGraphFrame( GUIManager.getInstance().getFrame( graph), graph);
	}
	
}
