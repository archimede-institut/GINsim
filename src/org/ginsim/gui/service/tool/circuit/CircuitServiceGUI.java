package org.ginsim.gui.service.tool.circuit;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.exception.NotificationMessage;
import org.ginsim.exception.NotificationMessageHolder;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.GsToolsAction;
import org.ginsim.service.tool.circuit.CircuitService;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.common.managerresources.Translator;

/**
 * main method for the circuit service
 * 
 * @author Aurelien Naldi
 */
@ProviderFor( ServiceGUI.class)
@GUIFor( CircuitService.class)
public class CircuitServiceGUI implements ServiceGUI {

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof RegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new CircuitAction((RegulatoryGraph)graph));
			return actions;
		}
		return null;
	}
}

class CircuitAction extends GsToolsAction {

	private final RegulatoryGraph graph;
	
	public CircuitAction(RegulatoryGraph graph) {
		
		super("STR_circuit");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
        if (graph.getNodeOrderSize() < 1) {
            new NotificationMessage( (NotificationMessageHolder) graph, Translator.getString("STR_emptyGraph"), NotificationMessage.NOTIFICATION_WARNING);
            return;
        }

        new CircuitFrame( GUIManager.getInstance().getFrame( graph), graph);
	}
	
}