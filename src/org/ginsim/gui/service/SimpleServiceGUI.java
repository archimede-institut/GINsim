package org.ginsim.gui.service;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.common.utils.log.LogManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.service.Service;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.core.utils.IntrospectionUtils;
import org.ginsim.gui.service.common.GUIFor;


public class SimpleServiceGUI<S extends Service> implements ServiceGUI {

	private final Class<? extends Action> actionClass;
	private final S service;
	private final int weight;
	
	public SimpleServiceGUI(Class<? extends Action> actionClass, int weight) {
		this.actionClass = actionClass;
		this.weight = weight;
		
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
	public int getWeight() {
		return weight;
	}

	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		
			List<Action> actions = new ArrayList<Action>();
			Action action;
			Constructor constructor;
			Class[] parameter_types;
			Object[] parameters;
			// Build the set of parameters types and parameters corresponding to the
			// desired constructor according to the context
			if( service == null){
				parameter_types = new Class[1];
				parameter_types[0] = IntrospectionUtils.getGraphInterface( graph.getClass());
				parameters = new Object[1];
				parameters[0] = graph;
			}
			else{
				parameter_types = new Class[2];
				parameter_types[0] = IntrospectionUtils.getGraphInterface( graph.getClass());
				parameter_types[1] = service.getClass();
				parameters = new Object[2];
				parameters[0] = graph;
				parameters[1] = service;
			}		
			
			// Try to call the right constructor to instantiate the action
			// If no constructor corresponds, it means no action is available for the given graph type
			try {
				constructor = actionClass.getConstructor( parameter_types);
				action = (Action)constructor.newInstance( parameters);
				actions.add(action);
			}
			catch( NoSuchMethodException nsme){
				actions = null;
			}
			catch( InvocationTargetException ite){
				actions = null;
			}
			catch( IllegalAccessException iae){
				actions = null;
			}
			catch( InstantiationException ie) {
				actions = null;
			}

			return actions;
	}
}
