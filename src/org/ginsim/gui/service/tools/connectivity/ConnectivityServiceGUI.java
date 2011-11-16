package org.ginsim.gui.service.tools.connectivity;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.graph.common.Graph;
import org.ginsim.graph.reducedgraph.GsReducedGraph;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.GsToolsAction;
import org.ginsim.service.action.connectivity.ConnectivityService;
import org.mangosdk.spi.ProviderFor;

/**
 * register the connectivity service
 */
@ProviderFor(GsServiceGUI.class)
@GUIFor(ConnectivityService.class)
public class ConnectivityServiceGUI implements GsServiceGUI {

	@Override
	public List<Action> getAvailableActions( Graph<?, ?> graph) {
		List<Action> actions = new ArrayList<Action>();
		actions.add( new ConnectivityAction( graph));
		
		return actions;
	}
}

class ConnectivityAction extends GsToolsAction {
	
	private final Graph graph;
	
	protected ConnectivityAction( Graph graph) {
        super( "STR_connectivityExtract", null, "STR_connectivityExtract_descr", null);
		this.graph = graph;
	}
	
	@Override
	public void actionPerformed( ActionEvent arg0) {
		
	    if (graph instanceof GsReducedGraph) {
            String s_ag = ((GsReducedGraph) graph).getAssociatedGraphID();
            if (s_ag != null) {
            	// TODO : REFACTORING ACTION
            	// Change the GsOpenAction
            	// TODO: pass the selection to getSelectedMap
            	// note: gd = old GraphDescriptor
    	        Graph subgraph = GsOpenAction.open(gd, null, ((GsReducedGraph)graph).getSelectedMap(), s_ag);
    	        if (subgraph != null) {
    	            GUIManager.getInstance().whatToDoWithGraph( subgraph, graph, true);
    	        }
            }
	    } 
	    else {
	    	// TODO: get the parent frame
            new ConnectivityFrame( null, graph);
	    }
	}
}
