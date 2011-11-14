package org.ginsim.gui.service.tools.dynamicanalyser;

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
import org.ginsim.service.action.dynamicanalyser.DynamicAnalyserService;
import org.mangosdk.spi.ProviderFor;

/**
 * offer some facilities to analyse the state transition graph.
 */
@ProviderFor( GsServiceGUI.class)
@GUIFor( DynamicAnalyserService.class)
public class DynamicAnalyserServiceGUI implements GsServiceGUI {
    
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof GsDynamicGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add( new DynamicAnalyserAction( (GsDynamicGraph)graph));
			return actions;
		}
		return null;
	}
	
}

class DynamicAnalyserAction extends GsToolsAction {

	private final GsDynamicGraph graph;
	
	public DynamicAnalyserAction( GsDynamicGraph graph) {
		
		super( "STR_searchPath", "STR_searchPath_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

        new GsDynamicSearchPathConfig( GUIManager.getInstance().getFrame( graph), (GsDynamicGraph)graph);
	}
	
}