package org.ginsim.servicegui.tool.dynamicanalyser;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.dynamicgraph.DynamicGraph;
import org.ginsim.gui.GUIManager;
import org.ginsim.service.tool.dynamicanalyser.DynamicAnalyserService;
import org.ginsim.servicegui.ServiceGUI;
import org.ginsim.servicegui.common.GUIFor;
import org.ginsim.servicegui.common.ToolAction;
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

	@Override
	public int getWeight() {
		return W_INFO + 12;
	}

}

class DynamicAnalyserAction extends ToolAction {

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