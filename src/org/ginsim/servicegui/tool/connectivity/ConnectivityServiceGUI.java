package org.ginsim.servicegui.tool.connectivity;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;

import org.ginsim.common.utils.GUIMessageUtils;
import org.ginsim.core.exception.GsException;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.reducedgraph.NodeReducedData;
import org.ginsim.core.graph.reducedgraph.ReducedGraph;
import org.ginsim.core.utils.log.LogManager;
import org.ginsim.gui.GUIManager;
import org.ginsim.service.tool.connectivity.ConnectivityService;
import org.ginsim.servicegui.ServiceGUI;
import org.ginsim.servicegui.common.GUIFor;
import org.ginsim.servicegui.common.GenericGraphAction;
import org.ginsim.servicegui.common.ToolAction;
import org.mangosdk.spi.ProviderFor;


/**
 * register the connectivity service
 */
@ProviderFor(ServiceGUI.class)
@GUIFor(ConnectivityService.class)
public class ConnectivityServiceGUI implements ServiceGUI {

	@Override
	public List<Action> getAvailableActions( Graph<?, ?> graph) {
		List<Action> actions = new ArrayList<Action>();
		if (graph instanceof ReducedGraph) {
			actions.add( new ConnectivityExtractAction( (ReducedGraph)graph));
		} else {
			actions.add( new ConnectivityAction( graph));
		}
		
		return actions;
	}

	@Override
	public int getWeight() {
		return W_GENERIC;
	}
}

class ConnectivityExtractAction extends ToolAction {
	
	private final ReducedGraph graph;
	
	protected ConnectivityExtractAction( ReducedGraph graph) {
        super( "STR_connectivityExtract", null, "STR_connectivityExtract_descr", null);
		this.graph = graph;
	}
	
	@Override
	public void actionPerformed( ActionEvent arg0) {
		
        String s_ag = null;
		try {
			s_ag = graph.getAssociatedGraphID();
		} catch (GsException e) {
			LogManager.debug();
			return;
		}
		
        if (s_ag != null) {
        	List<NodeReducedData> selected = GUIManager.getInstance().getGraphGUI(graph).getSelection().getSelectedNodes();
        	
        	try{
		        Graph subgraph = GraphManager.getInstance().open( graph.getSelectedMap(selected), new File( s_ag));
		        if (subgraph != null) {
		            GUIManager.getInstance().whatToDoWithGraph( subgraph, graph, true);
		        }
        	}
        	catch( GsException gse){
        		LogManager.error( "Unable to execute the action");
        		LogManager.error(gse);
        		GUIMessageUtils.openErrorDialog( "STR_connectivity_UnableToExecuteAssociatedGraphNull");
        	}
        }
	}
}


class ConnectivityAction extends GenericGraphAction {
	
	protected ConnectivityAction( Graph graph) {
        super( graph, "STR_connectivity", null, "STR_connectivity_descr", null);
	}
	
	@Override
	public void actionPerformed( ActionEvent arg0) {
        new ConnectivityFrame( GUIManager.getInstance().getFrame(graph), graph);
	}
}
