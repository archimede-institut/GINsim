package org.ginsim.gui.service.tool.omddViz;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.common.Graph;
import org.ginsim.graph.regulatorygraph.RegulatoryGraph;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GsToolsAction;
import org.ginsim.gui.service.common.StandaloneGUI;
import org.mangosdk.spi.ProviderFor;


@ProviderFor(ServiceGUI.class)
@StandaloneGUI
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
}

class OMDDVizAction extends GsToolsAction {

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