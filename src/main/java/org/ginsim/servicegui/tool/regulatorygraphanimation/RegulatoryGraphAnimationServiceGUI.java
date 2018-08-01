package org.ginsim.servicegui.tool.regulatorygraphanimation;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.Graph;
import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.core.service.EStatus;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.shell.actions.GenericGraphAction;
import org.ginsim.core.service.ServiceStatus;
import org.ginsim.gui.service.StandaloneGUI;
import org.kohsuke.MetaInfServices;

/**
 * register the aRegGraph plugin: animate the regulatory graph 
 * according to a path in the associated state transition graph.
 */
@MetaInfServices( ServiceGUI.class)
@StandaloneGUI
@ServiceStatus( EStatus.RELEASED)
public class RegulatoryGraphAnimationServiceGUI extends AbstractServiceGUI {
    
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		
		if (graph instanceof DynamicGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new AnimRegGraphAction((DynamicGraph)graph, this));
			return actions;
		}
		return null;
	}

	@Override
	public int getInitialWeight() {
		return W_TOOLS_MAIN + 140;
	}

	class AnimRegGraphAction extends GenericGraphAction {

		private AnimRegGraphAction( DynamicGraph graph, ServiceGUI serviceGUI) {
			super( graph, "STR_aRegGraph", "STR_aRegGraph_descr", serviceGUI);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			RegulatoryAnimator.animate( GUIManager.getInstance().getFrame( graph), (DynamicGraph) graph);
		}
	}
}

