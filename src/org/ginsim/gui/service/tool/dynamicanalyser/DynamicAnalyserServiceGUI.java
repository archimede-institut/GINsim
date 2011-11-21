package org.ginsim.gui.service.tool.dynamicanalyser;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.common.Graph;
import org.ginsim.graph.dynamicgraph.DynamicGraph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.GsToolsAction;
import org.ginsim.service.tool.dynamicanalyser.DynamicAnalyserService;
import org.mangosdk.spi.ProviderFor;

/**
 * offer some facilities to analyse the state transition graph.
 */
@ProviderFor( ServiceGUI.class)
@GUIFor( DynamicAnalyserService.class)
public class DynamicAnalyserServiceGUI implements ServiceGUI {
    
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof DynamicGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add( new DynamicAnalyserAction( (DynamicGraph)graph));
			return actions;
		}
		return null;
	}
	
}

class DynamicAnalyserAction extends GsToolsAction {

	private final DynamicGraph graph;
	
	public DynamicAnalyserAction( DynamicGraph graph) {
		
		super( "STR_searchPath", "STR_searchPath_descr");
		this.graph = graph;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

        new DynamicSearchPathConfig( GUIManager.getInstance().getFrame( graph), (DynamicGraph)graph);
	}
	
}