package org.ginsim.servicegui.tool.regulatorygraphanimation;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.gui.GUIManager;
import org.ginsim.service.tool.regulatorygraphanimation.RegulatoryGraphAnimationService;
import org.ginsim.servicegui.ServiceGUI;
import org.ginsim.servicegui.common.GUIFor;
import org.ginsim.servicegui.common.ToolAction;
import org.mangosdk.spi.ProviderFor;

/**
 * register the aRegGraph plugin: animate the regulatory graph 
 * according to a path in the associated state transition graph.
 */
@ProviderFor( ServiceGUI.class)
@GUIFor( RegulatoryGraphAnimationService.class)
public class RegulatoryGraphAnimationServiceGUI implements ServiceGUI {
    
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		
		if (graph instanceof DynamicGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new AnimRegGraphAction((DynamicGraph)graph));
			return actions;
		}
		return null;
	}

	@Override
	public int getWeight() {
		return W_MAIN + 2;
	}
}

class AnimRegGraphAction extends ToolAction {

	private final DynamicGraph graph;
	
	public AnimRegGraphAction( DynamicGraph graph) {
		
		super( "STR_aRegGraph", "STR_aRegGraph_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

        RegulatoryAnimator.animate( GUIManager.getInstance().getFrame( graph), graph);
	}
	
}
