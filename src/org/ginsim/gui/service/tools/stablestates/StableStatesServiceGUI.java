package org.ginsim.gui.service.tools.stablestates;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.exception.NotificationMessage;
import org.ginsim.exception.NotificationMessageHolder;
import org.ginsim.graph.Graph;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.GsToolsAction;
import org.ginsim.service.action.stablestates.StableStatesService;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.common.managerresources.Translator;

/**
 * Define the action for stableStates service
 */
@ProviderFor( GsServiceGUI.class)
@GUIFor( StableStatesService.class)
public class StableStatesServiceGUI implements GsServiceGUI {
    
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		
		if (graph instanceof GsRegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new StableStatesAction( (GsRegulatoryGraph)graph));
			return actions;
		}
		return null;
	}
}

class StableStatesAction extends GsToolsAction {

	private final GsRegulatoryGraph graph;
	
	public StableStatesAction(GsRegulatoryGraph graph) {
		
		super( "STR_stableStates", "STR_stableStates_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		
    	if (graph.getNodeOrderSize() < 1) {
            new NotificationMessage( (NotificationMessageHolder) graph, Translator.getString("STR_emptyGraph"), NotificationMessage.NOTIFICATION_WARNING);
    		return;
    	}

    	GsStableStateUI ui = new GsStableStateUI( graph);
    	ui.setVisible(true);
	}
	
}
