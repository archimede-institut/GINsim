package org.ginsim.gui.service.tools.connectivity;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.exception.GsException;
import org.ginsim.graph.common.Graph;
import org.ginsim.graph.reducedgraph.GsNodeReducedData;
import org.ginsim.graph.reducedgraph.GsReducedGraph;
import org.ginsim.gui.FileSelectionHelper;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.service.GsServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.GsToolsAction;
import org.ginsim.service.action.connectivity.ConnectivityService;
import org.mangosdk.spi.ProviderFor;

import fr.univmrs.tagc.common.Debugger;

/**
 * register the connectivity service
 */
@ProviderFor(GsServiceGUI.class)
@GUIFor(ConnectivityService.class)
public class ConnectivityServiceGUI implements GsServiceGUI {

	@Override
	public List<Action> getAvailableActions( Graph<?, ?> graph) {
		List<Action> actions = new ArrayList<Action>();
		if (graph instanceof GsReducedGraph) {
			actions.add( new ConnectivityExtractAction( (GsReducedGraph)graph));
		} else {
			actions.add( new ConnectivityAction( graph));
		}
		
		return actions;
	}
}

class ConnectivityExtractAction extends GsToolsAction {
	
	private final GsReducedGraph graph;
	
	protected ConnectivityExtractAction( GsReducedGraph graph) {
        super( "STR_connectivityExtract", null, "STR_connectivityExtract_descr", null);
		this.graph = graph;
	}
	
	@Override
	public void actionPerformed( ActionEvent arg0) {
		
        String s_ag = null;
		try {
			s_ag = graph.getAssociatedGraphID();
		} catch (GsException e) {
			Debugger.log();
			return;
		}
		
        if (s_ag != null) {
        	List<GsNodeReducedData> selected = GUIManager.getInstance().getGraphGUI(graph).getSelection().getSelectedNodes();
        	
	        Graph subgraph = FileSelectionHelper.open( s_ag, graph.getSelectedMap(selected));
	        if (subgraph != null) {
	            GUIManager.getInstance().whatToDoWithGraph( subgraph, graph, true);
	        }
        }
	}
}


class ConnectivityAction extends GsToolsAction {
	
	private final Graph graph;
	
	protected ConnectivityAction( Graph graph) {
        super( "STR_connectivity", null, "STR_connectivity_descr", null);
		this.graph = graph;
	}
	
	@Override
	public void actionPerformed( ActionEvent arg0) {
        new ConnectivityFrame( GUIManager.getInstance().getFrame(graph), graph);
	}
}
