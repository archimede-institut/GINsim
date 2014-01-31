package org.ginsim.servicegui.tool.dynamicanalyser;

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
import org.mangosdk.spi.ProviderFor;

/**
 * offer some facilities to analyse the state transition graph.
 */
@StandaloneGUI
@ProviderFor( ServiceGUI.class)
@ServiceStatus( EStatus.DEVELOPMENT)
public class DynamicAnalyserServiceGUI extends AbstractServiceGUI {
    
	@Override
	public List<Action> getAvailableActions(Graph<?, ?> graph) {
		if (graph instanceof DynamicGraph) {
			List<Action> actions = new ArrayList<Action>();
			actions.add( new DynamicAnalyserAction( (DynamicGraph)graph, this));
			return actions;
		}
		return null;
	}

	@Override
	public int getInitialWeight() {
		return W_GRAPH_SELECTION + 25;
	}

}

class DynamicAnalyserAction extends GenericGraphAction {
	
	public DynamicAnalyserAction( DynamicGraph graph, ServiceGUI serviceGUI) {
		
		super( graph, "STR_searchPath", null, "STR_searchPath_descr", null, serviceGUI);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

        new DynamicSearchPathConfig( GUIManager.getInstance().getFrame( graph), (DynamicGraph)graph);
	}
	
}