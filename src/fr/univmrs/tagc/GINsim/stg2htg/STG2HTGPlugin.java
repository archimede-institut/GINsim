package fr.univmrs.tagc.GINsim.stg2htg;

import javax.swing.JFrame;

import org.ginsim.exception.GsException;
import org.ginsim.graph.Graph;

import fr.univmrs.tagc.GINsim.css.Selector;
import fr.univmrs.tagc.GINsim.gui.GsPluggableActionDescriptor;

public class STG2HTGPlugin implements GsPlugin, GsActionProvider{

	private GsPluggableActionDescriptor[] t_action = null;

	public void registerPlugin() {
		GsGraph.registerActionProvider(this);
		Selector.registerSelector(STG2HTGSelector.IDENTIFIER, STG2HTGSelector.class);
	}
	
	public GsPluggableActionDescriptor[] getT_action(int actionType, Graph graph) {
		
		if (actionType != ACTION_ACTION) {
			return null;
		}
		if (t_action == null) {
			t_action = new GsPluggableActionDescriptor[1];
			t_action[0] = new GsPluggableActionDescriptor("STR_STG2HTG", "STR_STG2HTG_descr", null, this, ACTION_ACTION, 0);
		}
		return t_action;
	}
	
	public void runAction(int actionType, int ref, Graph graph, JFrame frame) throws GsException {
		
		if (actionType != ACTION_ACTION) {
			return;
		}
		if (ref == 0) {
			Thread thread = new STG2HTG(frame, graph);
			thread.start();
		}
	}

}

