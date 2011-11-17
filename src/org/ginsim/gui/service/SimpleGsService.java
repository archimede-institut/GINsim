package org.ginsim.gui.service;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.common.Graph;

import fr.univmrs.tagc.common.Debugger;

public class SimpleGsService<A extends Action> implements GsServiceGUI {

	private final Class<A> actionClass;
	
	public SimpleGsService(Class<A> actionClass) {
		this.actionClass = actionClass;
	}

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
			List<Action> actions = new ArrayList<Action>();
			for (Constructor constructor: actionClass.getConstructors()) {
				try {
					Action action = (Action)constructor.newInstance(graph);
					actions.add(action);
				} catch (Exception e) {
					Debugger.log(e);
				}
			}

			if (actions.size() == 0) {
				actions = null;
			}
			return actions;
	}
}
