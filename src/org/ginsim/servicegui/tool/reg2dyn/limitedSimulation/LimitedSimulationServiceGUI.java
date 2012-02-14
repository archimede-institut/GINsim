package org.ginsim.servicegui.tool.reg2dyn.limitedSimulation;


import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.hierachicaltransitiongraph.HierarchicalTransitionGraph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.gui.service.common.ToolAction;
import org.ginsim.service.tool.decisionanalysis.DecisionAnalysisService;
import org.mangosdk.spi.ProviderFor;




@ProviderFor( ServiceGUI.class)
@GUIFor( DecisionAnalysisService.class)
@ServiceStatus( ServiceStatus.RELEASED)
public class LimitedSimulationServiceGUI implements ServiceGUI {
	
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof HierarchicalTransitionGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new LimitedSimulationAction((HierarchicalTransitionGraph)graph));
			return actions;
		}
		return null;
	}

	@Override
	public int getWeight() {
		return W_MAIN + 20;
	}
}


class LimitedSimulationAction extends ToolAction {
	private static final long serialVersionUID = -2719039497869822805L;
	private final HierarchicalTransitionGraph graph;
	
	public LimitedSimulationAction ( HierarchicalTransitionGraph graph) {
		
		super( "STR_limitedSimulation", "STR_limitedSimulation_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		new LimitedSimulationFrame( GUIManager.getInstance().getFrame( graph), graph);
	}
	
}
