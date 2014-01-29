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
import org.ginsim.gui.shell.actions.GenericGraphAction;
import org.ginsim.gui.service.ServiceStatus;
import org.ginsim.gui.service.StandaloneGUI;
import org.mangosdk.spi.ProviderFor;


@StandaloneGUI
@ProviderFor( ServiceGUI.class)
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

	private static final long serialVersionUID = -3180174464155997775L;

	public StateInRegulatoryGraphAction(RegulatoryGraph graph, ServiceGUI serviceGUI) {
		
		super( graph, "STR_stateInRegGraph", null, "STR_stateInRegGraph_descr", null, serviceGUI);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

        new StateInRegGraphFrame( GUIManager.getInstance().getFrame( graph), graph);
	}
	
}
