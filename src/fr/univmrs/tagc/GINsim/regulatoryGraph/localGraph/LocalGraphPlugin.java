package fr.univmrs.tagc.GINsim.regulatoryGraph.localGraph;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;

import org.ginsim.graph.Graph;
import org.ginsim.gui.service.GsServiceGUI;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.GINsim.css.Selector;
import fr.univmrs.tagc.GINsim.dynamicGraph.GsDynamicGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;

@ProviderFor(GsServiceGUI.class)
public class LocalGraphPlugin implements GsServiceGUI {

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

class LocalGraphAction extends AbstractAction {

	private final GsRegulatoryGraph graph;
	private final GsDynamicGraph dyn;
	
	protected LocalGraphAction(GsRegulatoryGraph graph) {
		this(graph, null);
	}
	protected LocalGraphAction(GsDynamicGraph graph) {
		this(graph.getAssociatedGraph(), graph);
	}
	protected LocalGraphAction(GsRegulatoryGraph graph, GsDynamicGraph dyn) {
		super("STR_localGraph");
		this.graph = graph;
		this.dyn = dyn;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO: find the parent frame
		if (dyn == null) {
			new LocalGraphFrame(null, graph);
		} else {
			new LocalGraphFrame(null, graph, dyn);
		}
	}
}
