package org.ginsim.gui.service.tool.stablestates;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.notification.Notification;
import org.ginsim.core.notification.WarningNotification;

import org.ginsim.graph.common.Graph;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.resource.Translator;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.ToolAction;
import org.ginsim.gui.utils.dialog.stackdialog.HandledStackDialog;
import org.ginsim.service.tool.stablestates.StableStatesService;
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
            new WarningNotification( graph, Translator.getString("STR_emptyGraph"));
    		return;
    	}

    	new HandledStackDialog( new StableStateUI( graph));
	}
	
}
