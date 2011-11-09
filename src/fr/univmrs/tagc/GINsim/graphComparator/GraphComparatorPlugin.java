package fr.univmrs.tagc.GINsim.graphComparator;

import javax.swing.JFrame;

import org.ginsim.exception.GsException;
import org.ginsim.graph.Graph;

import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;

// FIXME: make a service
public class GraphComparatorPlugin {

	private GsPluggableActionDescriptor[] t_action = null;

	public GsPluggableActionDescriptor[] getT_action(int actionType, Graph graph) {
		
		if (t_action == null) {
			t_action = new GsPluggableActionDescriptor[1];
			t_action[0] = new GsPluggableActionDescriptor("STR_gcmp", "STR_gcmp_descr", null, this, ACTION_ACTION, 0);
		}
		return t_action;
	}

	public void runAction(int actionType, int ref, Graph graph, JFrame frame) throws GsException {
		
		if (ref == 0) {
           new GraphComparatorFrame(frame, graph);
		}
	}
}
