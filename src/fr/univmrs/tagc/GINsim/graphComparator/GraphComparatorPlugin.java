package fr.univmrs.tagc.GINsim.graphComparator;

import javax.swing.JFrame;

import org.ginsim.graph.Graph;

import fr.univmrs.tagc.GINsim.graph.GsActionProvider;
import fr.univmrs.tagc.GINsim.graph.GsGraph;
import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;
import fr.univmrs.tagc.GINsim.plugin.GsPlugin;
import fr.univmrs.tagc.common.GsException;

public class GraphComparatorPlugin implements GsPlugin, GsActionProvider {

	private GsPluggableActionDescriptor[] t_action = null;

	public void registerPlugin() {
		GsGraph.registerActionProvider(this);
	}

	public GsPluggableActionDescriptor[] getT_action(int actionType, Graph graph) {
		
		if (actionType != ACTION_ACTION) {
			return null;
		}
		if (t_action == null) {
			t_action = new GsPluggableActionDescriptor[1];
			t_action[0] = new GsPluggableActionDescriptor("STR_gcmp", "STR_gcmp_descr", null, this, ACTION_ACTION, 0);
		}
		return t_action;
	}

	public void runAction(int actionType, int ref, Graph graph, JFrame frame) throws GsException {
		
		if (actionType != ACTION_ACTION) {
			return;
		}
		if (ref == 0) {
           new GraphComparatorFrame(frame, graph);
		}
	}
}
