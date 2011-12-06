package org.ginsim.servicegui.tool.circuit;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.notification.NotificationManager;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.resource.Translator;
import org.ginsim.service.tool.circuit.CircuitService;
import org.ginsim.servicegui.ServiceGUI;
import org.ginsim.servicegui.common.GUIFor;
import org.ginsim.servicegui.common.ToolAction;
import org.mangosdk.spi.ProviderFor;


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

class CircuitAction extends ToolAction {

	private final RegulatoryGraph graph;
	
	public CircuitAction(RegulatoryGraph graph) {
		
		super("STR_circuit");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
        if (graph.getNodeOrderSize() < 1) {
            NotificationManager.publishWarning( graph, Translator.getString("STR_emptyGraph"));
            return;
        }

        new CircuitFrame( GUIManager.getInstance().getFrame( graph), graph);
	}
	
}