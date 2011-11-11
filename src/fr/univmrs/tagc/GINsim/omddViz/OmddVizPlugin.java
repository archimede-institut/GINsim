package fr.univmrs.tagc.GINsim.omddViz;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.Graph;
import org.ginsim.gui.service.GsActionAction;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.gui.service.StandaloneGUI;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;

@ProviderFor(GsServiceGUI.class)
@StandaloneGUI
public class OmddVizPlugin implements GsServiceGUI {

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof GsRegulatoryGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new OMDDVizAction((GsRegulatoryGraph)graph));
			return actions;
		}
		return null;
	}
}

class OMDDVizAction extends GsActionAction {

	private GsRegulatoryGraph graph;

	public OMDDVizAction(GsRegulatoryGraph graph) {
		super("STR_omddViz", "STR_omddViz_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		new OmddVizFrame(graph);
	}
	
}