package org.ginsim.servicegui.tool.stablestates;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.notification.NotificationManager;
import org.ginsim.gui.resource.Translator;
import org.ginsim.gui.utils.dialog.stackdialog.HandledStackDialog;
import org.ginsim.service.tool.stablestates.StableStatesService;
import org.ginsim.servicegui.ServiceGUI;
import org.ginsim.servicegui.common.GUIFor;
import org.ginsim.servicegui.common.ToolAction;
import org.mangosdk.spi.ProviderFor;


/**
 * Define the action for stableStates service
 */
@ProviderFor( ServiceGUI.class)
@GUIFor( StableStatesService.class)
public class StableStatesServiceGUI implements ServiceGUI {
    
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		
		if (graph instanceof RegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new StableStatesAction( (RegulatoryGraph)graph));
			return actions;
		}
		return null;
	}

	@Override
	public int getWeight() {
		return W_ANALYSIS + 1;
	}
}

class StableStatesAction extends ToolAction {

	private final RegulatoryGraph graph;
	
	public StableStatesAction(RegulatoryGraph graph) {
		
		super( "STR_stableStates", "STR_stableStates_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
    	if (graph.getNodeOrderSize() < 1) {
            NotificationManager.publishWarning( graph, Translator.getString("STR_emptyGraph"));
    		return;
    	}

    	new HandledStackDialog( new StableStateUI( graph));
	}
	
}
