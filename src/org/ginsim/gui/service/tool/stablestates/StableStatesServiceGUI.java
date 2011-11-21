package org.ginsim.gui.service.tool.stablestates;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.exception.NotificationMessage;
import org.ginsim.exception.NotificationMessageHolder;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.GsToolsAction;
import org.ginsim.service.tool.stablestates.StableStatesService;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.common.gui.dialog.stackdialog.HandledStackDialog;
import fr.univmrs.tagc.common.managerresources.Translator;

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

class StableStatesAction extends GsToolsAction {

	private final RegulatoryGraph graph;
	
	public StableStatesAction(RegulatoryGraph graph) {
		
		super( "STR_stableStates", "STR_stableStates_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
    	if (graph.getNodeOrderSize() < 1) {
            new NotificationMessage( (NotificationMessageHolder) graph, Translator.getString("STR_emptyGraph"), NotificationMessage.NOTIFICATION_WARNING);
    		return;
    	}

    	new HandledStackDialog( new StableStateUI( graph));
	}
	
}
