package org.ginsim.servicegui.tool.connectivity;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.Action;

import org.ginsim.common.application.GsException;
import org.ginsim.common.application.LogManager;
import org.ginsim.commongui.dialog.GUIMessageUtils;
import org.ginsim.core.graph.GraphManager;
import org.ginsim.core.graph.common.Graph;
import org.ginsim.core.graph.reducedgraph.NodeReducedData;
import org.ginsim.core.graph.reducedgraph.ReducedGraph;
import org.ginsim.core.graph.view.css.Colorizer;
import org.ginsim.core.service.ServiceManager;
import org.ginsim.gui.GUIManager;
import org.ginsim.gui.graph.GraphGUI;
import org.ginsim.gui.graph.GraphSelection;
import org.ginsim.gui.service.AbstractServiceGUI;
import org.ginsim.gui.service.ServiceGUI;
import org.ginsim.gui.service.common.GUIFor;
import org.ginsim.gui.service.common.GenericGraphAction;
import org.ginsim.gui.service.common.ServiceStatus;
import org.ginsim.service.tool.connectivity.ConnectivityResult;
import org.ginsim.service.tool.connectivity.ConnectivitySelector;
import org.ginsim.service.tool.connectivity.ConnectivityService;
import org.mangosdk.spi.ProviderFor;


/**
 * register the connectivity service
 */
@ProviderFor(ServiceGUI.class)
@GUIFor(ConnectivityService.class)
@ServiceStatus( ServiceStatus.RELEASED)
public class ConnectivityServiceGUI extends AbstractServiceGUI {

	private int initialWeight = W_GRAPH_COLORIZE + 20;

	@Override
	public List<Action> getAvailableActions( Graph<?, ?> graph) {
		List<Action> actions = new ArrayList<Action>();
		if (graph instanceof ReducedGraph) {
			actions.add( new ExtractFromSCCGraphAction( (ReducedGraph)graph, this));
			initialWeight = W_TOOLS_MAIN + 35;
		} else {
			actions.add( new ConnectivityColorizeGraphAction( graph, this));
			initialWeight = W_GRAPH_COLORIZE + 20;
		}
		return actions;
	}

	@Override
	public int getInitialWeight() {
		return initialWeight;
	}
}

class ConnectivityColorizeGraphAction extends GenericGraphAction {
	private static final long serialVersionUID = 8294301473668672512L;
	private Colorizer colorizer;
	
	protected ConnectivityColorizeGraphAction( Graph graph, ServiceGUI serviceGUI) {
        super( graph, "STR_connectivity", null, "STR_connectivity_descr", null, serviceGUI);
        colorizer = new Colorizer(new ConnectivitySelector());

	}
	
	@Override
	public void actionPerformed( ActionEvent arg0) {
		ConnectivityService service = ServiceManager.getManager().getService(ConnectivityService.class);
        ConnectivityResult result = service.run(graph);
        ((ConnectivitySelector)colorizer.getSelector()).setCache(result.getComponents(), graph);
        colorizer.doColorize(graph);
        if (GUIManager.getInstance().getFrame(graph) == null) {
        		GUIManager.getInstance().whatToDoWithGraph(graph, true);
        }
	}
}

class ExtractFromSCCGraphAction extends GenericGraphAction {
	private static final long serialVersionUID = 8294301473668672512L;
	
	protected ExtractFromSCCGraphAction( ReducedGraph<?, ?, ?> graph, ServiceGUI serviceGUI) {
        super( graph, "STR_connectivity_extract", null, "STR_connectivity_extract_descr", null, serviceGUI);

	}
	
	@Override
	public void actionPerformed( ActionEvent arg0) {
		
		ReducedGraph<?, ?, ?> g = (ReducedGraph<?, ?, ?>)graph;
		GraphGUI<ReducedGraph<?, ?, ?>, NodeReducedData, ?> gui = GUIManager.getInstance().getGraphGUI(g);
		GraphSelection<NodeReducedData, ?> selection = gui.getSelection();
		
		if (selection == null || selection.getSelectedNodes() == null || selection.getSelectedNodes().size() < 1) {
			LogManager.debug("Select some nodes");
			GUIMessageUtils.openErrorDialog( "STR_unableToOpen_SeeLogs");
			return;
		}

		String s_ag = null;
		try {
			s_ag = g.getAssociatedGraphID();
		} catch (Exception e) {
			
		}
		if (s_ag == null) {
			LogManager.debug("Missing associated Graph");
			GUIMessageUtils.openErrorDialog( "STR_unableToOpen_SeeLogs");
			return;
		}
		
		File f = new File(s_ag);
		if (!f.exists()) {
			LogManager.debug("Missing associated Graph file: "+s_ag);
			GUIMessageUtils.openErrorDialog( "STR_unableToOpen_SeeLogs");
			return;
		}
		
		
	    Graph subgraph = null;
	    try {
			Set<?> set = g.getSelectedSet( selection.getSelectedNodes());
			subgraph = GraphManager.getInstance().open(set, f);
		} catch (GsException e) {
			e.printStackTrace();
		}
	    
	    if (subgraph == null) {
			LogManager.debug("SCC extraction led to a null graph");
			return;
	    }
	    
        GUIManager.getInstance().whatToDoWithGraph(subgraph, graph, true);
	}
}
