package fr.univmrs.tagc.GINsim.interactionAnalysis;

import javax.swing.JFrame;

import org.ginsim.graph.Graph;

import fr.univmrs.tagc.GINsim.css.Selector;
import fr.univmrs.tagc.GINsim.graph.GsActionProvider;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.tagc.GINsim.plugin.GsPlugin;
import fr.univmrs.tagc.GINsim.regulatoryGraph.GsRegulatoryGraphDescriptor;
import fr.univmrs.tagc.common.GsException;

public class InteractionAnalysisPlugin implements GsActionProvider, GsPlugin {

	private GsPluggableActionDescriptor[] t_action = null;

	public void registerPlugin() {
		GsRegulatoryGraphDescriptor.registerActionProvider(this);
		Selector.registerSelector(InteractionAnalysisSelector.IDENTIFIER, InteractionAnalysisSelector.class);
	}
	
	
	public GsPluggableActionDescriptor[] getT_action(int actionType, Graph graph) {
		if (actionType != ACTION_ACTION) {
			return null;
		}
		if (t_action == null) {
			t_action = new GsPluggableActionDescriptor[1];
			t_action[0] = new GsPluggableActionDescriptor("STR_interactionAnalysis", "STR_interactionAnalysis_descr", null, this, ACTION_ACTION, 0);
		}
		return t_action;
	}
	
	public void runAction(int actionType, int ref, Graph graph, JFrame frame) throws GsException {
		if (actionType != ACTION_ACTION) {
			return;
		}
		if (ref == 0) {
           new InteractionAnalysisFrame(frame, graph);
		}
	}
}
