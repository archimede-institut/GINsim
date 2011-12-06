package org.ginsim.servicegui;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.utils.log.LogManager;
import org.ginsim.service.Service;
import org.ginsim.service.ServiceManager;
import org.ginsim.servicegui.common.GUIFor;


public class SimpleServiceGUI<S extends Service> implements ServiceGUI {

	private final Class<? extends Action> actionClass;
	private final S service;
	
	public SimpleServiceGUI(Class<? extends Action> actionClass) {
		this.actionClass = actionClass;
		
		GUIFor backend = getClass().getAnnotation(GUIFor.class);
		if (backend == null) {
			this.service = null;
		} else {
			this.service = ServiceManager.get((Class<S>)backend.value());
			if (service == null) {
				LogManager.error("Could not retrieve backend service: "+ backend.value());
			}
		}
	}

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
			List<Action> actions = new ArrayList<Action>();
			Action action;
			for (Constructor constructor: actionClass.getConstructors()) {
				try {
					if (service == null) {
						action = (Action)constructor.newInstance(graph);
					} else {
						action = (Action)constructor.newInstance(graph, service);
					}
					actions.add(action);
				} catch (Exception e) {
					LogManager.error( e);
				}
			}

			if (actions.size() == 0) {
				actions = null;
			}
			return actions;
	}
}
