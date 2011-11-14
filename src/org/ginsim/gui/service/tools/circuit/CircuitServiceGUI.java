package org.ginsim.gui.service.tools.circuit;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.exception.NotificationMessage;
import org.ginsim.exception.NotificationMessageHolder;
import org.ginsim.graph.Graph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.GsToolsAction;
import org.ginsim.service.action.circuit.CircuitService;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.common.managerresources.Translator;

/**
 * main method for the circuit service
 * 
 * @author Aurelien Naldi
 */
@ProviderFor( GsServiceGUI.class)
@GUIFor( CircuitService.class)
public class CircuitServiceGUI implements GsServiceGUI {

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof GsRegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new CircuitAction((GsRegulatoryGraph)graph));
			return actions;
		}
		return null;
	}
}

class CircuitAction extends GsToolsAction {

	private final GsRegulatoryGraph graph;
	
	public CircuitAction(GsRegulatoryGraph graph) {
		
		super("STR_circuit");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
        if (graph.getNodeOrderSize() < 1) {
            new NotificationMessage( (NotificationMessageHolder) graph, Translator.getString("STR_emptyGraph"), NotificationMessage.NOTIFICATION_WARNING);
            return;
        }

        new GsCircuitFrame( GUIManager.getInstance().getFrame( graph), graph);
	}
	
}