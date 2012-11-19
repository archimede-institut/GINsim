package org.ginsim.servicegui.tool.decisionanalysis;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.common.application.GsException;
import org.ginsim.common.application.LogManager;
import org.ginsim.commongui.dialog.GUIMessageUtils;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.hierachicaltransitiongraph.HierarchicalTransitionGraph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.gui.service.common.StandaloneGUI;
import org.ginsim.gui.service.common.ToolAction;
import org.mangosdk.spi.ProviderFor;




@StandaloneGUI
@ProviderFor( ServiceGUI.class)
@ServiceStatus( ServiceStatus.RELEASED)
public class DecisionAnalysisServiceGUI extends AbstractServiceGUI {
	
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof HierarchicalTransitionGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new DecisionAnalysisAction((HierarchicalTransitionGraph)graph, this));
			return actions;
		}
		return null;
	}

	@Override
	public int getInitialWeight() {
		return W_TOOLS_MAIN + 120;
	}
}


class DecisionAnalysisAction extends ToolAction {
	private static final long serialVersionUID = -2719039497869822805L;
	private final HierarchicalTransitionGraph graph;
	
	public DecisionAnalysisAction( HierarchicalTransitionGraph graph, ServiceGUI serviceGUI) {
		
		super( "STR_htg_decision_analysis", "STR_htg_decision_analysis_descr", serviceGUI);
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		try{
			new DecisionAnalysisFrame( GUIManager.getInstance().getFrame( graph), graph);
		}
		catch( GsException ge){
    		GUIMessageUtils.openErrorDialog( "Unable to launch the analysis");
    		LogManager.error( "Unable to execute the service");
    		LogManager.error( ge);
		}
	}
	
}
