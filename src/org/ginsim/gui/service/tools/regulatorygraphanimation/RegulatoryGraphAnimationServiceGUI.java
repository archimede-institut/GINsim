package org.ginsim.gui.service.tools.regulatorygraphanimation;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.common.Graph;
import org.ginsim.graph.dynamicgraph.GsDynamicGraph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.GsToolsAction;
import org.ginsim.service.action.regulatorygraphanimation.RegulatoryGraphAnimationService;
import org.mangosdk.spi.ProviderFor;

/**
 * register the aRegGraph plugin: animate the regulatory graph 
 * according to a path in the associated state transition graph.
 */
@ProviderFor( GsServiceGUI.class)
@GUIFor( RegulatoryGraphAnimationService.class)
public class RegulatoryGraphAnimationServiceGUI implements GsServiceGUI {
    
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		
		if (graph instanceof GsDynamicGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add(new AnimRegGraphAction((GsDynamicGraph)graph));
			return actions;
		}
		return null;
	}
}

class AnimRegGraphAction extends GsToolsAction {

	private final GsDynamicGraph graph;
	
	public AnimRegGraphAction( GsDynamicGraph graph) {
		
		super( "STR_aRegGraph", "STR_aRegGraph_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

        GsRegulatoryAnimator.animate( GUIManager.getInstance().getFrame( graph), graph);
	}
	
}