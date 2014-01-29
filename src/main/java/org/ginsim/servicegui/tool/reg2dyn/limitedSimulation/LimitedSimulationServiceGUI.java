package org.ginsim.servicegui.tool.reg2dyn.limitedSimulation;


import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.common.application.GsException;
import org.ginsim.common.application.LogManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.hierarchicaltransitiongraph.HierarchicalTransitionGraph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.ServiceStatus;
import org.ginsim.gui.service.StandaloneGUI;
import org.ginsim.gui.shell.actions.ToolAction;
import org.mangosdk.spi.ProviderFor;


@StandaloneGUI
@ProviderFor( ServiceGUI.class)
@ServiceStatus( ServiceStatus.RELEASED)
public class LimitedSimulationServiceGUI extends AbstractServiceGUI {
	
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof HierarchicalTransitionGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new LimitedSimulationAction((HierarchicalTransitionGraph)graph, this));
			return actions;
		}
		return null;
	}

	@Override
	public int getInitialWeight() {
		return W_TOOLS_MAIN + 130;
	}
}


class LimitedSimulationAction extends ToolAction {
	private static final long serialVersionUID = -2719039497869822805L;
	private final HierarchicalTransitionGraph graph;
	
	public LimitedSimulationAction ( HierarchicalTransitionGraph graph, ServiceGUI serviceGUI) {
		
		super( "STR_limitedSimulation", "STR_limitedSimulation_descr", serviceGUI);
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		RegulatoryGraph lrg;
		try {
			lrg = graph.getAssociatedGraph();
			new LimitedSimulationFrame( GUIManager.getInstance().getFrame( graph), graph, lrg);
		} catch (GsException ex) {
			LogManager.error("The htg is not associated with a regulatory graph");
		}
	}
	
}
