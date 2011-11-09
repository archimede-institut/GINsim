package fr.univmrs.tagc.GINsim.regulatoryGraph.localGraph;

import javax.swing.JFrame;

import org.ginsim.exception.GsException;
import org.ginsim.graph.Graph;

import fr.univmrs.tagc.GINsim.css.Selector;
import fr.univmrs.tagc.GINsim.dynamicGraph.GsDynamicGraph;
import fr.univmrs.tagc.GINsim.dynamicGraph.GsDynamicGraphDescriptor;
import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraph;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraphDescriptor;

public class LocalGraphPlugin implements GsActionProvider, GsPlugin {

	private GsPluggableActionDescriptor[] t_action = null;

	public void registerPlugin() {
		GsRegulatoryGraphDescriptor.registerActionProvider(this);
		GsDynamicGraphDescriptor.registerActionProvider(this);
		Selector.registerSelector(LocalGraphSelector.IDENTIFIER, LocalGraphSelector.class);
	}
	
	
	public GsPluggableActionDescriptor[] getT_action(int actionType, Graph graph) {
		if (actionType != ACTION_ACTION) {
			return null;
		}
		if (t_action == null) {
			t_action = new GsPluggableActionDescriptor[1];
			t_action[0] = new GsPluggableActionDescriptor("STR_localGraph", "STR_localGraph_descr", null, this, ACTION_ACTION, 0);
		}
		return t_action;
	}
	
	public void runAction(int actionType, int ref, Graph graph, JFrame frame) throws GsException {
		if (actionType != ACTION_ACTION) {
			return;
		}
		if (ref == 0) {
			if (graph instanceof GsRegulatoryGraph) new LocalGraphFrame(frame, graph);
			if (graph instanceof GsDynamicGraph) new LocalGraphFrame(frame, ((GsDynamicGraph) graph).getAssociatedGraph(), graph);
		}
	}
}
