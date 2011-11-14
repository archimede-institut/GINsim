package org.ginsim.gui.service.tools.localgraph;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.Graph;
import org.ginsim.graph.dynamicgraph.GsDynamicGraph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.GsToolsAction;
import org.ginsim.service.action.localgraph.LocalGraphService;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.css.Selector;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;

@ProviderFor( GsServiceGUI.class)
@GUIFor( LocalGraphService.class)
public class LocalGraphServiceGUI implements GsServiceGUI {

	static {
		Selector.registerSelector(LocalGraphSelector.IDENTIFIER, LocalGraphSelector.class);
	}
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		List<Action> actions = new ArrayList<Action>();
		if (graph instanceof GsRegulatoryGraph) {
			actions.add(new LocalGraphAction((GsRegulatoryGraph)graph));
		} else if (graph instanceof GsDynamicGraph){
			actions.add(new LocalGraphAction((GsDynamicGraph)graph));
		}
		return actions;
	}
}

class LocalGraphAction extends GsToolsAction {

	private final GsRegulatoryGraph graph;
	private final GsDynamicGraph dyn;
	
	protected LocalGraphAction(GsRegulatoryGraph graph) {
		this(graph, null);
	}
	protected LocalGraphAction(GsDynamicGraph graph) {
		this(graph.getAssociatedGraph(), graph);
	}
	protected LocalGraphAction(GsRegulatoryGraph graph, GsDynamicGraph dyn) {
		super("STR_localGraph", "STR_localGraph_descr");
		this.graph = graph;
		this.dyn = dyn;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if (dyn == null) {
			new LocalGraphFrame( GUIManager.getInstance().getFrame( graph), graph);
		} else {
			new LocalGraphFrame( GUIManager.getInstance().getFrame( graph), graph, dyn);
		}
	}
}
