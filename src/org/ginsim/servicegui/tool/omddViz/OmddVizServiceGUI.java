package org.ginsim.servicegui.tool.omddViz;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.gui.service.common.StandaloneGUI;
import org.ginsim.gui.service.common.ToolAction;
import org.mangosdk.spi.ProviderFor;


@ProviderFor(ServiceGUI.class)
@StandaloneGUI
@ServiceStatus( ServiceStatus.UNDER_DEVELOPMENT)
public class OmddVizServiceGUI implements ServiceGUI {

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof RegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new OMDDVizAction((RegulatoryGraph)graph));
			return actions;
		}
		return null;
	}

	@Override
	public int getWeight() {
		return W_INFO + 3;
	}
}

class OMDDVizAction extends ToolAction {

	private RegulatoryGraph graph;

	public OMDDVizAction(RegulatoryGraph graph) {
		super("STR_omddViz", "STR_omddViz_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		new OmddVizFrame(graph);
	}
	
}