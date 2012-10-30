package org.ginsim.servicegui.tool.circuit;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.common.application.Translator;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.core.notification.NotificationManager;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.gui.service.common.ToolAction;
import org.ginsim.service.tool.circuit.CircuitService;
import org.mangosdk.spi.ProviderFor;


/**
 * main method for the circuit service
 * 
 * @author Aurelien Naldi
 */
@ProviderFor( ServiceGUI.class)
@GUIFor( CircuitService.class)
@ServiceStatus( ServiceStatus.RELEASED)
public class CircuitServiceGUI extends AbstractServiceGUI {

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof RegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new CircuitAction((RegulatoryGraph)graph, this));
			return actions;
		}
		return null;
	}

	@Override
	public int getInitialWeight() {
		return ServiceGUI.W_TOOLS_MAIN + 60;
	}
}

class CircuitAction extends ToolAction {

	private final RegulatoryGraph graph;
	
	public CircuitAction(RegulatoryGraph graph, ServiceGUI serviceGUI) {
		
		super("STR_circuit", serviceGUI);
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