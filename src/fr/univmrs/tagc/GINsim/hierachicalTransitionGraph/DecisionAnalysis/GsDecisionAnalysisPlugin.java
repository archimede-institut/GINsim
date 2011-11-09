package fr.univmrs.tagc.GINsim.hierachicalTransitionGraph.DecisionAnalysis;

import javax.swing.JFrame;

import org.ginsim.exception.GsException;
import org.ginsim.graph.Graph;

import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.tagc.GINsim.hierachicalTransitionGraph.GsHierarchicalTransitionGraphDescriptor;

public class GsDecisionAnalysisPlugin implements GsActionProvider, GsPlugin {

	private GsPluggableActionDescriptor[] t_action = null;

	public void registerPlugin() {
		GsHierarchicalTransitionGraphDescriptor.registerActionProvider(this);
	}
	
	
	public GsPluggableActionDescriptor[] getT_action(int actionType, Graph graph) {
		if (actionType != ACTION_ACTION) {
			return null;
		}
		if (t_action == null) {
			t_action = new GsPluggableActionDescriptor[1];
			t_action[0] = new GsPluggableActionDescriptor("STR_htg_decision_analysis", "STR_htg_decision_analysis_descr", null, this, ACTION_ACTION, 0);
		}
		return t_action;
	}
	
	public void runAction(int actionType, int ref, Graph graph, JFrame frame) throws GsException {
		if (actionType != ACTION_ACTION) {
			return;
		}
		if (ref == 0) {
           new GsDecisionAnalysisFrame(frame, graph);
		}
	}
}
